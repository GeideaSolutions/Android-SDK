package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.phone

import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.model.bnpl.valu.VerifyCustomerResponse

internal sealed class ValuPhoneNumberState(open val nextButtonEnabled: Boolean) {
    class Idle(
            nextButtonEnabled: Boolean = false
    ) : ValuPhoneNumberState(nextButtonEnabled = nextButtonEnabled)

    object Loading : ValuPhoneNumberState(nextButtonEnabled = false)

    data class Success(val response: VerifyCustomerResponse) : ValuPhoneNumberState(
            nextButtonEnabled = true
    )

    data class Error(
            override val nextButtonEnabled: Boolean,
            val message: NativeText?,
    ) : ValuPhoneNumberState(nextButtonEnabled = nextButtonEnabled)
}