package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.toGeideaResult
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.internal.ui.fragment.base.NavCommands
import net.geidea.paymentsdk.internal.ui.fragment.base.Snack
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.EventObserver
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.pay.PaymentResponse
import java.math.BigDecimal

internal abstract class BaseCardPaymentViewModel(
    // Dependencies
    internal val paymentViewModel: PaymentViewModel,
    private val paymentService: PaymentService,
    private val cancellationService: CancellationService,
    private val merchantConfiguration: MerchantConfigurationResponse,
    internal val downPaymentAmount: BigDecimal?,
    protected val step: Step? = null,
) : ViewModel(), NavCommands by paymentViewModel {
    // Session state

    // The initialData with any user-overridden data (addresses, e-mail, phone, etc). This is the
    // data that will be used for authentication.
    protected lateinit var finalPaymentData: PaymentData

    protected val isFinalPaymentDataInitialized: Boolean get() = ::finalPaymentData.isInitialized

    var orderId: String?
        internal set(newValue) {
            paymentViewModel.orderId = newValue
        }

        get() = paymentViewModel.orderId
    var sessionId: String?
        internal set(newValue) {
            paymentViewModel.sessionId = newValue
        }

        get() = paymentViewModel.sessionId

    /**
     * Session ID for the 3DS authentication process.
     */
    var threeDSecureId: String? = null
        internal set

    private val _failedOnceLiveData = MutableLiveData(false)
    val failedOnceLiveData: LiveData<Boolean> = _failedOnceLiveData

    private val clientTimeoutObserver = EventObserver<Unit> {
        end3dsSession()

        // Has an input form in the back stack?
        val formDestinationId = getInputFormDestinationId()
        if (formDestinationId != 0) {
            paymentViewModel.navigateBackTo(formDestinationId)
        }
    }

    init {
        // Start session only in Standalone payment (not for BNPL down payment where we have a
        // preexisting order), because at end of session (client timed out) the current orderId
        // will be cleared and this forces next auth call to generate a new order.
        if (downPaymentAmount == null) {
            paymentViewModel.startClientTimeoutTimer()
            paymentViewModel.clientTimeoutLiveEvent.observeForever(clientTimeoutObserver)
        }
    }

    override fun onCleared() {
        super.onCleared()
        paymentViewModel.clientTimeoutLiveEvent.removeObserver(clientTimeoutObserver)
    }

    @CallSuper
    protected open fun end3dsSession() {
        this.threeDSecureId = null
    }

    /*private fun cancelOrderIfNeeded() {
        val orderId = this@BaseCardPaymentViewModel.orderId
        val isBnplDownPayment = step != null
        if (orderId != null && (orderId != initialPaymentData.orderId || isBnplDownPayment)) {
            // Order is implicitly created after entering card flow, it is not pre-existing
            // Use global scope to allow calling on fragment destruction outside of VM scope
            GlobalScope.launch {
                try {
                    cancelOrder(
                        orderId = orderId,
                        reason = CancelRequest.REASON_CANCELLED_BY_USER
                    )
                } catch (e: IOException) {
                    loge("Failed to cancel order $orderId due to connectivity error {$e.message}.")
                } catch (e: Exception) {
                    loge("Failed to cancel order $orderId due to ${e.message}")
                } finally {
                    this@BaseCardPaymentViewModel.orderId = null
                }
            }
        }
    }*/

    private suspend fun cancelOrder(orderId: String, reason: String): PaymentResponse {
        val cancelRequest = CancelRequest {
            this.orderId = orderId
            this.reason = reason
        }
        return cancellationService.postCancel(cancelRequest)
    }

    /**
     * Cancel the current order with explicit call to [CancellationService] and then finish with the
     * last error.
     */
    fun cancelAndFinish() {
        viewModelScope.launch {
            finishOnCatch {
                val result = if (orderId != null) {
                    cancelOrder(
                        orderId = orderId!!,
                        reason = CancelRequest.REASON_CANCELLED_BY_USER
                    )

                    val lastError: GeideaResult.NetworkError? =
                        paymentViewModel.getResult() as? GeideaResult.NetworkError?
                    if (lastError != null) {
                        GeideaResult.Cancelled(
                            responseCode = lastError.responseCode,
                            responseMessage = lastError.responseMessage,
                            detailedResponseCode = lastError.detailedResponseCode,
                            detailedResponseMessage = lastError.detailedResponseMessage,
                            orderId = orderId,
                        )
                    } else {
                        paymentViewModel.makeDefaultCancelledResult(orderId)
                    }
                } else {
                    paymentViewModel.makeDefaultCancelledResult(orderId)
                }

                paymentViewModel.onPaymentFinished(result)
                paymentViewModel.orderId = null
            }
        }
    }

    /**
     * Encloses a code [block] that must never throw. Instead finish with the error.
     */
    internal suspend fun finishOnCatch(block: suspend () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            loge(t.stackTraceToString())
            paymentViewModel.onPaymentFinished(t.toGeideaResult())
            end3dsSession()
        }
    }

    /**
     * Return the destination id of this payment flow input form or CVV input screen where user can be
     * returned in case of failed transaction in order to retry. Otherwise, user is returned back
     * to the app.
     */
    @IdRes
    protected abstract fun getInputFormDestinationId(): Int

    internal fun handleFailureResponse(failResponse: GeideaResponse, orderId: String?) {
        val error = GeideaResult.NetworkError(orderId = orderId, response = failResponse)
        handleNetworkError(error, errorSnack(failResponse))
    }

    internal fun handleNetworkError(error: GeideaResult.NetworkError, errorSnack: Snack) {
        handleNetworkError(
            error = error,
            responseCode = error.responseCode,
            detailedResponseCode = error.detailedResponseCode,
            errorSnack = errorSnack,
        )
    }

    private fun handleNetworkError(error: GeideaResult<Order>, responseCode: String?, detailedResponseCode: String?, errorSnack: Snack) {
        val isPaymentBlocked = responseCode == "600"
        val isOrderTimedOut = responseCode == "100" && detailedResponseCode == "030"
        val isRecoverableError = !isPaymentBlocked && !isOrderTimedOut

        // Has an input form in the back stack?
        val formDestinationId = getInputFormDestinationId()
        if (formDestinationId != 0 && isRecoverableError) {
            markAsFailedOnce()
            paymentViewModel.setResult(error)
            paymentViewModel.showSnack(errorSnack)
            // Back to the input form
            paymentViewModel.navigateBackTo(formDestinationId)
        } else {
            // No payment form shown. Cannot let user to retry, so return to app
            paymentViewModel.onPaymentFinished(error)
        }
    }

    internal fun markAsFailedOnce() {
        _failedOnceLiveData.value = true
    }

    abstract fun onPayButtonClicked(finalPaymentData: PaymentData)

    abstract fun onPayerAuthenticationStarted(viewModelScope: CoroutineScope, userAgent: UserAgent)

    abstract fun handleSuccessReturnUrl(orderId: String, sessionId: String?, urlParams: ReturnUrlParams)
}