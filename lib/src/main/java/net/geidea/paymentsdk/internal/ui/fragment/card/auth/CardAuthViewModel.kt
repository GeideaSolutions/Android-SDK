package net.geidea.paymentsdk.internal.ui.fragment.card.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.util.Logger
import net.geidea.paymentsdk.internal.util.parseQueryParams
import java.net.URLDecoder

@GeideaSdkInternal
internal class CardAuthViewModel(
        private val cardPaymentViewModel: BaseCardPaymentViewModel,
) : BaseViewModel(), UserAgent {

    private val _htmlLiveData = MutableLiveData<String>()
    val htmlLiveData: LiveData<String> = _htmlLiveData

    override lateinit var deviceInfo: DeviceInfo
        private set

    fun start(deviceInfo: DeviceInfo) {
        this.deviceInfo = deviceInfo
        cardPaymentViewModel.onPayerAuthenticationStarted(viewModelScope, this)
    }

    override fun loadHtml(html: String) {
        _htmlLiveData.value = html
    }

    fun onBackPressed() {
        if (cardPaymentViewModel.orderId != null) {
            val defaultCancelledResult = cardPaymentViewModel.paymentViewModel.makeDefaultCancelledResult(orderId = cardPaymentViewModel.orderId)
            cardPaymentViewModel.paymentViewModel.showSnack(errorSnack(failResponse = defaultCancelledResult))
        }
        navigateBack()
    }

    /*override fun navigateBack() {
        cardPaymentViewModel.cancelOrderIfNeeded()
        super.navigateBack()
    }*/

    fun onCancelConfirmed() {
        cardPaymentViewModel.cancelAndFinish()
    }

    fun onReturnUrl(url: String): Boolean {
        Logger.logi("onReturnUrl($url)")
        return if (url.isGeideaReturnUrl()) {
            val urlParams: ReturnUrlParams = parseUrlResult(url)
            val orderId = cardPaymentViewModel.orderId
            val sessionId = cardPaymentViewModel.sessionId
            if (urlParams.isSuccess && orderId != null) {
                cardPaymentViewModel.handleSuccessReturnUrl(orderId, sessionId, urlParams)
            } else {
                handleFailureReturnUrl(urlParams)
            }

            // We recognized and consumed the url, tell WebView to not load it
            true
        } else {
            // Tell WebView to load the url normally
            return false
        }
    }

    private fun handleFailureReturnUrl(urlParams: ReturnUrlParams) {
        Logger.logd("handleFailureReturnUrl($urlParams)")

        val error = GeideaResult.NetworkError(
            orderId = cardPaymentViewModel.orderId,
            responseCode = urlParams.code,
            responseMessage = urlParams.msg,
        )

        cardPaymentViewModel.handleNetworkError(error, errorSnack(urlParams))
    }

    private fun String.isGeideaReturnUrl(): Boolean {
        return this.startsWith(ReturnUrlParams.RETURN_URL)
    }

    private fun parseUrlResult(url: String): ReturnUrlParams {
        val queryPairs: List<Pair<String, String>> = parseQueryParams(url)

        val codePair = queryPairs.firstOrNull { it.first == "code" }
        val msgPair = queryPairs.firstOrNull { it.first == "msg" }

        return ReturnUrlParams(
            code = codePair?.second?.let { URLDecoder.decode(it, "UTF-8") },
            msg = msgPair?.second?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }
}