package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.confirm

import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.NativeText

internal sealed class ShahryConfirmState(
    open val orderTokenInputEnabled: Boolean = true,
    open val buttonEnabled: Boolean = true,
    open val buttonTitle: NativeText = NativeText.Resource(R.string.gd_shahry_btn_confirm),
    open val progressVisible: Boolean = false,
    open val errorMessage: NativeText? = null,
    open val termsTextVisible: Boolean = true,
    open val payUpfrontVisible: Boolean = false,
    open val downPaymentText: NativeText? = null,
    open val purchaseFeesText: NativeText? = null,
    open val totalUpfrontText: NativeText? = null,
    open val downPaymentOptionsVisible: Boolean = false,
    open val downPaymentNowText: NativeText = NativeText.Empty,
    open val downPaymentOnDeliveryText: NativeText = NativeText.Empty,
    open val stepCount: Int = 2,
) {
    internal object Initial : ShahryConfirmState()

    internal object Confirming : ShahryConfirmState(
        orderTokenInputEnabled = false,
        buttonEnabled = false,
        buttonTitle = NativeText.Plain(""),      // Hide the text on button and show progress
        progressVisible = true
    )

    /**
     * Intermediate state before proceeding to finish or down payment.
     */
    internal object Confirmed : ShahryConfirmState()

    /**
     * Idle state awaiting user to proceed. This could be:
     * - without down payment
     * - with down payment now
     * - with down payment on delivery (a.k.a. cashOnDelivery)
     */
    internal class AwaitingToProceed(
        override val termsTextVisible: Boolean = false,
        override val payUpfrontVisible: Boolean = true,
        override val purchaseFeesText: NativeText?,
        override val downPaymentText: NativeText?,
        override val totalUpfrontText: NativeText?,
        override val downPaymentOptionsVisible: Boolean,
        override val downPaymentNowText: NativeText,
        override val downPaymentOnDeliveryText: NativeText,
        override val buttonEnabled: Boolean,
        override val buttonTitle: NativeText,
        override val stepCount: Int,
        val plan: ShahryInstallmentPlan,
    ) : ShahryConfirmState(
        orderTokenInputEnabled = false,
    )

    internal data class Error(override val errorMessage: NativeText) : ShahryConfirmState()
}