package net.geidea.paymentsdk.ui.validation

/**
 * Listener notified after the validation status of an input field has changed
 * from [undefined][ValidationStatus.Undefined] or [valid][ValidationStatus.Valid] to
 * [invalid][ValidationStatus.Invalid].
 *
 * Note: the listener is **not** notified when an [invalid][ValidationStatus.Invalid] value
 * is changed with another invalid value. For this case a text watcher could be used instead.
 *
 * @see Validator
 */
fun interface OnInvalidStatusListener<T> {

    /**
     * Called when value becomes [invalid][ValidationStatus.Invalid].
     *
     * @param value the current invalid value
     * @param validationStatus the concrete status
     */
    fun onInvalidStatus(value: T?, validationStatus: ValidationStatus.Invalid)
}