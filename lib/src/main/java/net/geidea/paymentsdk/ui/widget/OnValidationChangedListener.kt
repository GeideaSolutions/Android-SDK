package net.geidea.paymentsdk.ui.widget

/**
 * Listener called when the validation status of an input field or a form has changed.
 *
 * @param T type of the value which is validated.
 */
fun interface OnValidationChangedListener<T> {

    /**
     * Called when the validation status has changed.
     *
     * @param value the new value
     * @param valid validation status
     */
    fun onValidationChanged(value: T?, valid: Boolean)
}