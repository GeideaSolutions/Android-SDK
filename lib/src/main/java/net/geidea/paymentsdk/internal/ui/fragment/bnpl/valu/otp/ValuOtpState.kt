package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.otp

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.model.bnpl.valu.ConfirmResponse
import net.geidea.paymentsdk.model.bnpl.valu.GenerateOtpResponse

@GeideaSdkInternal
internal sealed interface ValuOtpState {
    object Initial : ValuOtpState
    object Loading : ValuOtpState
    data class OtpSent(val response: GenerateOtpResponse) : ValuOtpState
    data class PurchaseSuccess(val response: ConfirmResponse) : ValuOtpState
    data class Error(val message: CharSequence?) : ValuOtpState
}