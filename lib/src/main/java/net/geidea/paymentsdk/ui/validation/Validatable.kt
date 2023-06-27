package net.geidea.paymentsdk.ui.validation

import net.geidea.paymentsdk.ui.widget.OnErrorListener

/**
 * Abstraction of an input widget (normally text field) whose content value is validatable by a
 * [validator][net.geidea.paymentsdk.ui.validation.Validator] associated to the widget.
 *
 * @param V the internal type of the input value (e.g. String for EditText)
 * @param T the external type of the input value
 * (e.g. a [Country][net.geidea.paymentsdk.model.Country] for
 * [CountryAutoCompleteTextView][net.geidea.paymentsdk.ui.widget.address.CountryAutoCompleteTextView].
 */
interface Validatable<V, T> {

    /**
     * Updates the current validation status with the validator. If no validator is set method has
     * no effect.
     */
    fun validate()

    /**
     * Returns true if the value in this field is valid or false otherwise.
     * Equivalent to `validationStatus == ValidationStatus.Valid`.
     */
    val isValid: Boolean

    /**
     * Current validation status. Implementor must keep this status in sync with
     * the current text input.
     */
    val validationStatus: ValidationStatus

    /**
     * Set listener to be notified when the current input text has
     * become [valid][ValidationStatus.Valid] from previously
     * being [invalid][ValidationStatus.Invalid] or [undefined][ValidationStatus.Undefined].
     *
     * Note: a validator should be set before in order to receive status updates.
     *
     * @param listener listener to be set
     */
    fun setOnValidStatusListener(listener: OnValidStatusListener<T>?)

    /**
     * Set listener to be notified when the current input text has
     * become [invalid][ValidationStatus.Invalid] from previously
     * being [valid][ValidationStatus.Valid] or [undefined][ValidationStatus.Undefined].
     *
     * Note: a validator should be set before in order to receive status updates.
     *
     * @param listener listener to be set
     */
    fun setOnInvalidStatusListener(listener: OnInvalidStatusListener<T>?)

    /**
     * Updates or clears the error message with the current [validation status][validationStatus].
     * Clears it when valid.
     */
    fun updateErrorMessage()

    /**
     * Set listener responsible for displaying the field error message.
     */
    fun setOnErrorListener(listener: OnErrorListener)

    /**
     * Boolean flag controlling whether an error message is automatically set when this
     * field loses focus and the value is invalid. Default is true.
     */
    var isErrorShownOnFocusLost: Boolean
}