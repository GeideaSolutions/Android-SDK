package net.geidea.paymentsdk.ui.widget.otp

/**
 * Listener notified when the string content of [OtpInputView] has changed.
 */
fun interface OnOtpChangedListener {
    /**
     * Called when the string content of [OtpInputView] has changed.
     *
     * @param otp the currently input OTP string
     * @param filled true if [otp] is [OtpInputView.expectedLength] long
     */
    fun onOtpChanged(otp: String, filled: Boolean)
}