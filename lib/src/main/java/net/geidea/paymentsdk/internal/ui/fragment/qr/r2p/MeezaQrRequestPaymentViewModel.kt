package net.geidea.paymentsdk.internal.ui.fragment.qr.r2p

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.MeezaService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.successSnack
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentResponse
import net.geidea.paymentsdk.ui.validation.receiver.MeezaMobileNumberOrDigitalIdValidator

@Deprecated("From the old flow which is not used anymore")
@GeideaSdkInternal
internal class MeezaQrRequestPaymentViewModel(
        private val paymentViewModel: PaymentViewModel,
        private val meezaService: MeezaService,
        private val connectivity: NetworkConnectivity,
        private val qrMessage: String,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<MeezaQrRequestPaymentState>()
    val stateLiveData: LiveData<MeezaQrRequestPaymentState> = _stateLiveData

    fun onSendButtonClicked(receiverId: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = MeezaQrRequestPaymentState.Sending

            val request = MeezaPaymentRequest {
                this.merchantPublicKey = GeideaPaymentSdk.merchantKey
                //TODO: impl. meeza phone number
                this.receiverId = MeezaMobileNumberOrDigitalIdValidator.getNormalizedReceiverId(receiverId)!!
                this.qrCodeMessage = qrMessage
            }
            when (val result: GeideaResult<MeezaPaymentResponse> = responseAsResult { meezaService.postRequestToPay(request) }) {
                is GeideaResult.Success -> {
                    showSnack(successSnack(NativeText.Resource(R.string.gd_meezaqr_r2p_payment_request_sent)))
                    _stateLiveData.value = MeezaQrRequestPaymentState.Success
                }
                is GeideaResult.Error -> {
                    showSnack(errorSnack(result))
                    _stateLiveData.value = MeezaQrRequestPaymentState.Error
                    paymentViewModel.setResult(result)
                }
                is GeideaResult.Cancelled -> {
                    // Nothing to do
                }
            }
        }
    }
}