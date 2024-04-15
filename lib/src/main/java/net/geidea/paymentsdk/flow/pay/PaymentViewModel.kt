package net.geidea.paymentsdk.flow.pay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.geidea.paymentsdk.GdNavigationDirections
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentActivity.Companion.EXTRA_RESULT
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.MerchantsService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.NavigationCommand
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.review.SouhoolaReviewFragmentDirections
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.plan.ValuInstallmentPlanFragmentDirections
import net.geidea.paymentsdk.internal.ui.fragment.receipt.ReceiptArgs
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.getReason
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import java.io.IOException

/**
 * View model of the checkout session.
 */
@GeideaSdkInternal
internal class PaymentViewModel(
    private val merchantsService: MerchantsService,
    private val cancellationService: CancellationService,
    private val paymentMethodsFilterProvider: () -> PaymentMethodsFilter,
    internal val nativeTextFormatter: NativeTextFormatter,
    // Runtime arguments
    val initialPaymentData: PaymentData,
) : BaseViewModel() {

    val merchantConfiguration: MerchantConfigurationResponse get() = merchantsService.cachedMerchantConfiguration

    // Session state data

    var currentPaymentMethod: PaymentMethodDescriptor? = null
    var bnplPaymentMethod: BnplPaymentMethodDescriptor? = null

    /**
     * Order ID for the current checkout process. Generally, the payment methods will create
     * an order and will keep its orderId in this reference.
     */
    var orderId: String? = null

    /**
     * Session ID for the current checkout process. Each payment transaction should have a unique
     * session ID associated with it.
     */
    var sessionId: String? = null

    val acceptedCardBrandNames: Set<String>? get() =
        if (initialPaymentData.paymentOptions != null) {
            paymentMethodsFilterProvider().acceptedCardBrands.map(CardBrand::name).toSet()
        } else {
            // No PaymentOptions set means no restriction on card brand
            null
        }

    private val _resultLiveData = MutableLiveData<GeideaResult<Order>>()
    val resultLiveData: LiveData<GeideaResult<Order>> = _resultLiveData

    private val _processingLiveData = MutableLiveData<Boolean>(false)
    val processingLiveData: LiveData<Boolean> = _processingLiveData

    var processing: Boolean
        get() = _processingLiveData.value ?: false
        set(value) {
            _processingLiveData.value = value
        }

    private var clientTimeoutTimer: Timer? = null

    private val _clientTimeoutLiveEvent = MutableLiveEvent<Unit>()
    val clientTimeoutLiveEvent: LiveEvent<Unit> = _clientTimeoutLiveEvent

    /**
     * Pre-load the config early. This blocks the Main thread but fragment creation
     * and restoration depend on it. A worker thread initialization would be better but
     * navigator restoration happens as early as onStart().
     */
    fun preloadMerchantConfiguration(): GeideaResult<MerchantConfigurationResponse> {
        Logger.logi("Preloading merchant configuration")
        return runBlocking {
            responseAsResult {
                merchantsService.getMerchantConfiguration(GeideaPaymentSdk.merchantKey)
            }
        }
    }

    /**
     * Set a result to be returned when the checkout finishes.
     */
    fun setResult(result: GeideaResult<Order>) {
        _resultLiveData.value = result
    }

    fun getResult(): GeideaResult<Order>? {
        return _resultLiveData.value
    }

    internal fun startClientTimeoutTimer() {
        clientTimeoutTimer?.cancel()
        clientTimeoutTimer = Timer(getOrderClientTimeoutMillis(), ::handleClientTimeout)
        clientTimeoutTimer?.start()
    }

    private fun handleClientTimeout() {
        viewModelScope.launch {
            val cancelledOrderId = this@PaymentViewModel.orderId
            try {
                val cancelRequest = CancelRequest {
                    orderId = this@PaymentViewModel.orderId
                    reason = CancelRequest.REASON_TIMED_OUT
                }
                cancellationService.postCancel(cancelRequest)
                orderId = null
                Logger.logi("Timed out order ${cancelRequest.orderId} cancelled successfully")
            } catch (e: Exception) {
                // Even if cancellation failed, server will cancel it anyway
                Logger.logi("Timed out order $orderId cancellation failed")
            }

            _clientTimeoutLiveEvent.value = Event(Unit)

            startClientTimeoutTimer()

            val timedOutResult = makeDefaultCancelledResult(cancelledOrderId)
            val timedOutSnack = errorSnack(
                title = plainText("010"),
                message = resourceText(R.string.gd_err_msg_payment_timed_out),
                duration = Snackbar.LENGTH_INDEFINITE
            )

            setResult(timedOutResult)
            showSnack(timedOutSnack)
        }
    }

    fun clearSession() {
        this.bnplPaymentMethod = null
        //this.orderId = null
    }

    fun onPaymentMethodSelected(
        paymentMethod: PaymentMethodDescriptor,
        isBnplDownPayment: Boolean
    ) {
        if (!isBnplDownPayment) {
            currentPaymentMethod = paymentMethod
            if (paymentMethod is BnplPaymentMethodDescriptor) {
                bnplPaymentMethod = paymentMethod
            }
        }
    }

    /**
     * Called by payment methods when they complete their flow and should give control back to the
     * main (this) view model.
     */
    fun onPaymentFinished(result: GeideaResult<Order>) {
        setResult(result)

        when (result) {
            is GeideaResult.Success -> when (bnplPaymentMethod) {
                // Down payment mode. Proceed with the rest of the BNPL flow.
                BnplPaymentMethodDescriptor.ValuInstallments -> {
                    navigateBackTo(R.id.gd_valuinstallmentplanfragment)
                    navigate(
                        navDirections = ValuInstallmentPlanFragmentDirections.gdActionGdValuinstallmentplanfragmentToGdValuotpfragment(
                            step = Step(current = 4, stepCount = 4)
                        ),
                        navOptions = navOptions {
                            popUpTo(R.id.gd_valuphonenumberfragment) {
                                inclusive = true
                            }
                        }
                    )
                }
                BnplPaymentMethodDescriptor.ShahryInstallments -> {
                    // Down payment mode. Proceed with the rest of the BNPL flow.
                    navigateBackTo(R.id.gd_confirmfragment)
                    navigateToReceiptOrFinish()
                }
                BnplPaymentMethodDescriptor.SouhoolaInstallments -> {
                    // Down payment mode. Proceed with the rest of the BNPL flow.
                    navigateBackTo(R.id.gd_souhoolareviewfragment)
                    navigate(
                        navDirections = SouhoolaReviewFragmentDirections.gdActionGdSouhoolareviewfragmentToGdSouhoolaotpfragment(
                            step = Step(current = 5, stepCount = 5)
                        ),
                        navOptions = navOptions {
                            popUpTo(R.id.gd_souhoolaverifycustomerfragment) {
                                inclusive = true
                            }
                        }
                    )
                }
                null -> navigateToReceiptOrFinish()
            }
            else -> navigateToReceiptOrFinish()
        }
    }

    /**
     * Handle 'Cancel' navigation command.
     *
     * Finish with [GeideaResult.Cancelled] as [EXTRA_RESULT]. If a [GeideaResult.NetworkError]
     * happened before cancellation, the response codes and messages of the last error will be
     * returned in the result.
     */
    fun onCancel() {
        if (resultLiveData.value is GeideaResult.NetworkError) {
            // TODO return info for other types of errors as well
            val error = resultLiveData.value as GeideaResult.NetworkError
            setResult(
                GeideaResult.Cancelled(
                    responseCode = error.responseCode,
                    responseMessage = error.responseMessage,
                    detailedResponseCode = error.detailedResponseCode,
                    detailedResponseMessage = error.detailedResponseMessage,
                    language = error.language,
                    orderId = error.orderId,
                )
            )
        }
        navigateToReceiptOrFinish()
    }

    internal fun shouldShowReceiptScreen(result: GeideaResult<Order>?): Boolean {
        val merchantConfiguration = merchantsService.cachedMerchantConfiguration
        val receiptWanted = initialPaymentData.showReceipt
                ?: merchantConfiguration.isTransactionReceiptEnabled == true

        // Can't show receipt if error happened before PM is even select.
        // Receipt needs to know what payment method was used.
        val isPaymentMethodSelectedAlready = getOuterPaymentMethod() != null

        val cancelledWithAnError = result is GeideaResult.Cancelled
                && result.responseCode != ErrorCodes.CancelledInformationGroup.code     // 010

        return receiptWanted
                && isPaymentMethodSelectedAlready
                && (result is GeideaResult.Error
                || result is GeideaResult.Success
                || cancelledWithAnError
                )
    }

    /**
     * Proceed to a final (receipt) screen if needed or finish if not.
     */
    fun navigateToReceiptOrFinish() {
        val result = _resultLiveData.value
        if (shouldShowReceiptScreen(result)) {
            val directions = when (result) {
                is GeideaResult.Success -> {
                    GdNavigationDirections.gdActionGlobalReceipt(
                        args = ReceiptArgs.Success(
                            paymentMethodDescriptor = getOuterPaymentMethod()!!,
                            orderId = result.data.orderId,
                            merchantReferenceId = result.data.merchantReferenceId,
                        )
                    )
                }
                else -> {
                    val resultOrderId = (result as? GeideaResult.NetworkError?)?.orderId
                    GdNavigationDirections.gdActionGlobalReceipt(
                        args = ReceiptArgs.Error(
                            paymentMethodDescriptor = getOuterPaymentMethod()!!,
                            orderId = resultOrderId ?: this.orderId,
                            merchantReferenceId = initialPaymentData.merchantReferenceId,
                            reason = nativeTextFormatter.format(getReason(result)),
                        )
                    )
                }
            }

            navigate(directions, navOptions = navOptions {
                popUpTo(R.id.gd_navigation) { inclusive = true }
            })
        } else {
            navigate(NavigationCommand.Finish)
        }
    }

    /**
     * The outer payment method is the one first selected by user. In case of BNPL,
     * the down-payment method is inner and the outer is the BNPL method.
     */
    private fun getOuterPaymentMethod(): PaymentMethodDescriptor? {
        return bnplPaymentMethod ?: currentPaymentMethod
    }

    override fun onCleared() {
        // Do not cancel order but save the orderId in PaymentActivity to reuse it after activity is recreated.
        if (shouldCancelOrder()) {
            GlobalScope.launch {
                try {
                    cancellationService.postCancel(CancelRequest {
                        this.orderId = this@PaymentViewModel.orderId
                        this.reason = CancelRequest.REASON_CANCELLED_BY_USER
                    })
                } catch (e: IOException) {
                    Logger.loge("Failed to cancel order $orderId due to connectivity error: {$e.message}.")
                } catch (e: Exception) {
                    Logger.loge("Failed to cancel order $orderId due to: ${e.message}")
                }
            }
        }
    }

    /**
     * Cancel only the BNPL orders because they cannot be reused (paid) with other payments later.
     */
    private fun shouldCancelOrder(): Boolean {
        return _resultLiveData.value !is GeideaResult.Success
                && orderId != null
                && bnplPaymentMethod != null
    }

    /**
     * The SDK attempts to keep (across activity recreation) any already created order except if
     * it is a BNPL order.
     */
    fun shouldSaveOrderId(): Boolean {
        return orderId != null && bnplPaymentMethod == null
    }

    /**
     * Create a default result that is returned to the calling app when user cancels explicitly.
     * It might contain [orderId] if an order has already been created by some of the payment methods.
     */
    fun makeDefaultCancelledResult(orderId: String?): GeideaResult.Cancelled = GeideaResult.Cancelled(
        orderId = orderId,
        responseCode = ErrorCodes.CancelledInformationGroup.code,   // 010
        responseMessage = nativeTextFormatter.format(resourceText(R.string.gd_cancelled)).toString(),
        detailedResponseCode = ErrorCodes.CancelledInformationGroup.CancelledByUser,     // 001
        detailedResponseMessage = nativeTextFormatter.format(resourceText(R.string.gd_cancelled_by_user)).toString(),
    )

    private fun getOrderClientTimeoutMillis() =
        (merchantConfiguration.hppDefaultTimeout ?: 1_800) * 1_000L
}

