package net.geidea.paymentsdk.ui.widget

/**
 * Listener called when an input field has requested that validation error message
 * should be shown or hidden.
 */
fun interface OnErrorListener {

    /**
     * Called when validation error should be shown or hidden.
     *
     * @param errorMessage the display message of the validation error.
     * If null the implementor is responsible to clear the error from their UI.
     */
    fun onShowError(errorMessage: CharSequence?)
}