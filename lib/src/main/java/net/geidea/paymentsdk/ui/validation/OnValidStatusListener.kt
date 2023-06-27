package net.geidea.paymentsdk.ui.validation

/**
 * Listener notified after the validation status of an input field has changed
 * from [undefined][ValidationStatus.Undefined] or [invalid][ValidationStatus.Invalid] to
 * [valid][ValidationStatus.Valid].
 *
 * Note: the listener is **not** notified when a [valid][ValidationStatus.Valid] value
 * is changed with another valid value. For this case a text watcher could be used instead.
 *
 * @see Validator
 */
fun interface OnValidStatusListener<T> {

    /**
     * Called when value becomes [valid][ValidationStatus.Valid].
     *
     * @param value the current value, guaranteed to be valid.
     */
    fun onValidStatus(value: T)
}