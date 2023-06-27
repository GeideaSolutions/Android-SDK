package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.verify

import net.geidea.paymentsdk.internal.util.NativeText

internal sealed class ShahryVerifyState(open val nextButtonEnabled: Boolean) {
    object Initial : ShahryVerifyState(nextButtonEnabled = false)
    object Loading : ShahryVerifyState(nextButtonEnabled = false)
    object Success : ShahryVerifyState(nextButtonEnabled = true)
    data class Error(
            override val nextButtonEnabled: Boolean,
            val message: NativeText,
    ) : ShahryVerifyState(nextButtonEnabled = nextButtonEnabled)
}