package net.geidea.paymentsdk.internal.ui.fragment.hpp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.flow.toGeideaResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.OrderService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.util.Logger
import net.geidea.paymentsdk.internal.util.generateSignature
import net.geidea.paymentsdk.internal.util.getCurrentTimestamp
import net.geidea.paymentsdk.internal.util.parseQueryParams
import net.geidea.paymentsdk.model.auth.v6.AppearanceRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionResponse
import net.geidea.paymentsdk.model.auth.v6.StylesRequest
import org.json.JSONObject

@GeideaSdkInternal
internal class HppViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val orderService: OrderService
) : BaseViewModel() {

    private val _hppUrlLiveData = MutableLiveData<String>()
    val hppUrlLiveData: LiveData<String> = _hppUrlLiveData

    private val _sessionId = MutableLiveData<String>()
    val sessionId: LiveData<String> = _sessionId

    fun start(paymentData: PaymentData) {
        setProgressbarVisibility(true)
        viewModelScope.launch {
            finishOnCatch {
                createSession(paymentData)
            }
        }
    }

    private suspend fun createSession(paymentData: PaymentData): CreateSessionResponse {
        val timestamp = getCurrentTimestamp()
        val signature = generateSignature(
            publicKey = GeideaPaymentSdk.merchantKey,
            orderAmount = paymentData.amount,
            orderCurrency = paymentData.currency,
            merchantRefId = paymentData.merchantReferenceId,
            apiPass = GeideaPaymentSdk.merchantPassword,
            timestamp = timestamp
        )

        val initiateSessionRequest = CreateSessionRequest {
            this.amount = paymentData.amount
            this.currency = paymentData.currency
            this.timestamp = timestamp
            this.merchantReferenceId = paymentData.merchantReferenceId
            this.signature = signature
            this.callbackUrl = paymentData.callbackUrl
            this.paymentIntentId = paymentData.paymentIntentId
            this.paymentOperation = paymentData.paymentOperation
            this.cardOnFile = paymentData.cardOnFile == true
            this.returnUrl = paymentData.returnUrl ?: DEFAULT_RETURN_URL
            this.appearanceRequest = AppearanceRequest {
                this.showEmail = paymentData.showCustomerEmail
                this.showAddress = paymentData.showAddress
                this.receiptPage = paymentData.showReceipt
                this.styles = StylesRequest {
                    this.hideGeideaLogo = false
                }
            }
        }
        val response = SdkComponent.sessionV2Service.createSession(initiateSessionRequest)
        if (response.isSuccess) {
            _sessionId.value = response.session!!.id!!
        }
        return response
    }

    fun loadPaymentView(sessionId: String) {
        _hppUrlLiveData.value = "${GeideaPaymentSdk.serverEnvironment.hppUrl}${sessionId}"
    }

    fun onBackPressed() {
        navigateBack()
    }

    fun onReturnUrl(url: String): Boolean {
        return if (url.isReturnUrl()) {
            setProgressbarVisibility(true)
            val urlParams = parseUrlResult(url)
            val orderId = urlParams.optString("orderId")
            val responseCode = urlParams.optString("responseCode")
            val responseMessage = urlParams.optString("responseMessage")

            if (responseCode == "000" && responseMessage == "Success") {
                viewModelScope.launch {
                    loadOrder(orderId)
                    setProgressbarVisibility(false)
                    paymentViewModel.navigateToReceiptOrFinish()
                }
            } else {
                paymentViewModel.setResult(GeideaResult.NetworkError(orderId, responseMessage))
                paymentViewModel.navigateFinish()
            }
            true
        } else {
            // Tell WebView to load the url normally
            return false
        }
    }

    private suspend fun loadOrder(orderId: String) {
        val orderResult = responseAsResult(outputTransform = { it.order!! }) {
            orderService.getOrder(orderId)
        }
        when (orderResult) {
            is GeideaResult.Success -> {
                paymentViewModel.setResult(orderResult)
            }

            is GeideaResult.Error -> {
                paymentViewModel.setResult(orderResult)
            }

            is GeideaResult.Cancelled -> {
                // Must not reach here
            }
        }
    }

    private fun String.isReturnUrl(): Boolean {
        return paymentViewModel.initialPaymentData.callbackUrl?.let { this.startsWith(it) } ?: false || this.startsWith(
            DEFAULT_RETURN_URL
        )
    }

    private fun parseUrlResult(url: String): JSONObject {
        val queryPairs: List<Pair<String, String>> = parseQueryParams(url)
        return convertListToJson(queryPairs)
    }

    private fun convertListToJson(list: List<Pair<String, String>>): JSONObject {
        val jsonObject = JSONObject()
        for (pair in list) {
            jsonObject.put(pair.first, pair.second)
        }
        return jsonObject
    }

    private suspend fun finishOnCatch(block: suspend () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            paymentViewModel.processing = false
            Logger.loge(t.stackTraceToString())
            paymentViewModel.onPaymentFinished(t.toGeideaResult())
        }
    }

    fun setProgressbarVisibility(visible: Boolean) {
        paymentViewModel.processing = visible
    }

    companion object {
        private const val DEFAULT_RETURN_URL = "https://www.geidea.payments.returnurl/"
    }
}