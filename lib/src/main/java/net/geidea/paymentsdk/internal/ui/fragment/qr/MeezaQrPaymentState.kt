package net.geidea.paymentsdk.internal.ui.fragment.qr

internal sealed interface MeezaQrPaymentState {

    /**
     * On launch of the QR this state is skipped because the QR screen receives the image as a
     * navigation argument from the Payment Options screen. This state is activated only on press
     * of "Request to Pay" button which is possible after the old QR expires or due to an error.
     */
    object Generating : MeezaQrPaymentState

    /**
     * In this state QR is already generated and displayed. Here the view model polls for status
     * update until the QR payment intent reaches terminal status (success or error).
     *
     * @see net.geidea.paymentsdk.model.paymentintent.PaymentIntentStatus
     */
    data class Idle(
            val qrMessage: String,
            val qrCodeImageBase64: String,
            val paymentIntentId: String,
    ) : MeezaQrPaymentState

    /**
     * Common state for when the QR payment intent reaches some failure status.
     *
     * @see net.geidea.paymentsdk.model.paymentintent.PaymentIntentStatus
     */
    object Error : MeezaQrPaymentState
}