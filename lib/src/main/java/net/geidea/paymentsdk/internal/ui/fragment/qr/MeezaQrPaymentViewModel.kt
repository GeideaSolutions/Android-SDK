package net.geidea.paymentsdk.internal.ui.fragment.qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.MeezaService
import net.geidea.paymentsdk.internal.service.OrderService
import net.geidea.paymentsdk.internal.service.PaymentIntentService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.util.NativeTextFormatter
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.internal.util.resourceText
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentResponse
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentStatus
import java.math.BigDecimal

@GeideaSdkInternal
internal class MeezaQrPaymentViewModel(
        private val paymentViewModel: PaymentViewModel,
        private val meezaService: MeezaService,
        private val paymentIntentService: PaymentIntentService,
        private val orderService: OrderService,
        private val connectivity: NetworkConnectivity,
        private val formatter: NativeTextFormatter,
        private val downPaymentAmount: BigDecimal?,
        private val paymentIntentId: String,
        private val qrMessage: String,
        // TODO Although it is a small black and white PNG encoded as base64 string it might be
        // too large to pass as nav arg due to risk of TransactionTooLargeException. An IPC
        // transaction buffer is supposed to be 1024 KB but still...
        // Consider passing in different way (preferences, static instance, etc.).
        private val qrCodeImageBase64: String,
) : BaseViewModel() {

    internal val paymentData: PaymentData get() = paymentViewModel.initialPaymentData.copy(
        amount = downPaymentAmount ?: paymentViewModel.initialPaymentData.amount)

    private val _stateLiveData = MutableLiveData<MeezaQrPaymentState>()
    val stateLiveData: LiveData<MeezaQrPaymentState> = _stateLiveData

    fun start() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        // QR is already pre-generated in PaymentOptionsFragment as received here as nav args
        _stateLiveData.value = MeezaQrPaymentState.Idle(
            qrMessage = qrMessage,
            qrCodeImageBase64 = qrCodeImageBase64,
            paymentIntentId = paymentIntentId,
        )

        viewModelScope.launch {
            val orderResult = startPaymentIntentPolling(paymentIntentId = paymentIntentId)
            handleOrderResult(orderResult)
        }
    }

    /*fun onRequestToPayClicked() {
        generateQrCode()
    }

    private fun createMeezaPaymentIntentRequest(paymentData: PaymentData): CreateMeezaPaymentIntentRequest {
        val request = CreateMeezaPaymentIntentRequest {
            orderId = paymentViewModel.orderId
            merchantPublicKey = GeideaPaymentSdk.merchantKey
            amount = paymentData.amount
            currency = paymentData.currency
            if (!paymentData.customerEmail.isNullOrBlank()) {
                customer = CustomerRequest {
                    email = paymentData.customerEmail
                }
            }
            source = Source.MOBILE_APP
        }
        return request
    }

    private fun generateQrCode() {
        val request = createMeezaPaymentIntentRequest(paymentData = paymentData)
        generateQrCode(request)
    }

    private fun generateQrCode(request: CreateMeezaPaymentIntentRequest) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            _stateLiveData.value = MeezaQrPaymentState.Error(
                    qrCodeVisible = false,
                    errorMessage = NativeText.Resource(R.string.gd_err_msg_no_internet_connection),
                    retryButtonVisible = true,
                    requestPaymentButtonVisible = false,
            )
            return
        }

        _stateLiveData.value = MeezaQrPaymentState.Generating

        viewModelScope.launch {
            when (val qrResult = responseAsResult { meezaService.postCreateQrCodeImageBase64(request) }) {
                is GeideaResult.Success<MeezaQrImageResponse> -> {
                    _stateLiveData.value = MeezaQrPaymentState.QrGenerated(
                            qrMessage = qrResult.data.message!!,
                            qrCodeImageBase64 = qrResult.data.image!!,
                            paymentIntentId = qrResult.data.paymentIntentId!!
                    )

                    val orderResult = startPaymentIntentPolling(qrResult.data.paymentIntentId)
                    handleOrderResult(orderResult)
                }
                is GeideaResult.Error -> {
                    _stateLiveData.value = MeezaQrPaymentState.Error(
                            qrCodeVisible = false,
                            errorMessage = NativeText.Resource(R.string.gd_meezaqr_error_creating_qr),
                            retryButtonVisible = true,
                            requestPaymentButtonVisible = false,
                    )
                }
                is GeideaResult.Cancelled -> {
                    // Nothing to do
                }
            }
        }
    }*/

    /**
     * Start periodically getting a payment intent by id until its status
     * becomes [PaymentIntentStatus.PAID] or it expires or it reaches [MAX_POLL_COUNT] or tries.
     *
     * @return the last order created with this payment intent or null in case of error
     */
    private suspend fun startPaymentIntentPolling(paymentIntentId: String): GeideaResult.Success<Order>? {
        // Do a maximum of 200 requests - once every 3 seconds
        repeat(MAX_POLL_COUNT) {
            if (connectivity.isConnected) {
                val orderResult: GeideaResult.Success<Order>? = pollPaymentIntent(paymentIntentId)
                if (orderResult != null) {
                    return orderResult
                }
            } else {
                showSnack(noInternetSnack)
            }
            delay(POLL_PERIOD)
        }

        return null     // Timed out
    }

    private suspend fun pollPaymentIntent(paymentIntentId: String): GeideaResult.Success<Order>? {
        val pollResult = responseAsResult { paymentIntentService.getPaymentIntent(paymentIntentId) }
        when (pollResult) {
            is GeideaResult.Success<PaymentIntentResponse> -> {
                val response = pollResult.data

                val orderId: String? = response.paymentIntent.orders?.let { it.lastOrNull()?.orderId }
                paymentViewModel.orderId = orderId

                when (response.paymentIntent.status) {
                    PaymentIntentStatus.CREATED -> {
                        // Do nothing and continue polling until status changes
                    }
                    PaymentIntentStatus.PAID -> {
                        // Success
                        val orderResult: GeideaResult<Order> = responseAsResult(outputTransform = { it.order!! }) {
                            orderService.getOrder(orderId!!)
                        }

                        when (orderResult) {
                            is GeideaResult.Success -> return orderResult
                            is GeideaResult.Error -> paymentViewModel.setResult(orderResult)
                            is GeideaResult.Cancelled -> {}
                        }
                    }
                    PaymentIntentStatus.EXPIRED -> {
                        navigateBack()
                        val errorText = resourceText(R.string.gd_meezaqr_err_expired)
                        paymentViewModel.setResult(
                            GeideaResult.NetworkError(
                                orderId = orderId,
                                // Patch the technical error message with more user-friendly
                                responseMessage = formatter.format(errorText).toString()
                            )
                        )
                        paymentViewModel.showSnack(errorSnack(message = errorText))

                        /*_stateLiveData.value = MeezaQrPaymentState.Error(
                            qrCodeVisible = false,
                            errorMessage = errorText,
                            retryButtonVisible = true,
                            requestPaymentButtonVisible = false,
                        )*/
                    }
                    PaymentIntentStatus.INCOMPLETE -> {
                        navigateBack()
                        val errorText = resourceText(R.string.gd_meezaqr_err_incomplete)
                        paymentViewModel.setResult(
                            GeideaResult.NetworkError(
                                orderId = orderId,
                                responseMessage = formatter.format(errorText).toString()
                            )
                        )
                        paymentViewModel.showSnack(errorSnack(message = errorText))

                        /*_stateLiveData.value = MeezaQrPaymentState.Error(
                            qrCodeVisible = true,
                            errorMessage = NativeText.Resource(R.string.gd_meezaqr_err_msg_payment_incomplete),
                            retryButtonVisible = false,
                            requestPaymentButtonVisible = true
                        )*/
                    }
                    null -> {
                        paymentViewModel.setResult(GeideaResult.NetworkError(orderId = null, response))
                        // Could be a temporary problem, continue polling
                    }
                }
            }
            is GeideaResult.Error -> {
                paymentViewModel.setResult(pollResult)
                // Could be a temporary problem, continue polling
            }
            is GeideaResult.Cancelled -> {
                // Nothing to do
            }
        }

        return null
    }

    private fun handleOrderResult(orderResult: GeideaResult<Order>?) {
        if (orderResult != null) {
            paymentViewModel.onPaymentFinished(orderResult)
        } else {
            // Recoverable failure - client timeout
            _stateLiveData.value = MeezaQrPaymentState.Error
        }
    }

    /*fun onRequestPaymentButtonClicked() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        val state = stateLiveData.value
        if (state is MeezaQrPaymentState.QrGenerated) {
            navigate(NavigationCommand.ToDirection(
                    directions = MeezaQrPaymentFragmentDirections.gdActionGdMeezaqrpaymentfragmentToGdMeezaqrrequestpaymentfragment(
                    qrMessage = state.qrMessage
            )))
        }
    }*/

    companion object {
        // 400 * 3 sec = 1200 sec = 20 min. By default server expiry time is 15 min.
        private const val MAX_POLL_COUNT = 400
        private const val POLL_PERIOD = 3_000L
    }
}