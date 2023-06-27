package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.verify

import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.model.bnpl.souhoola.VerifyCustomerResponse

internal sealed class SouhoolaVerifyCustomerState(
    open val nextButtonEnabled: Boolean
) {
    class Idle(
            nextButtonEnabled: Boolean = true
    ) : SouhoolaVerifyCustomerState(nextButtonEnabled = nextButtonEnabled)

    object Loading : SouhoolaVerifyCustomerState(nextButtonEnabled = false)

    data class Success(val response: VerifyCustomerResponse) : SouhoolaVerifyCustomerState(
            nextButtonEnabled = true
    )

    data class Error(
        override val nextButtonEnabled: Boolean,
        val phoneError: NativeText?,
        val pinError: NativeText?,
    ) : SouhoolaVerifyCustomerState(nextButtonEnabled = nextButtonEnabled)
}