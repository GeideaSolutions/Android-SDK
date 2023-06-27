package net.geidea.paymentsdk.ui.widget.card

import net.geidea.paymentsdk.ui.validation.ValidationStatus

/**
 * Listener for card input events.
 */
interface CardInputListener {

    /**
     * Called when a field has received focus.
     *
     * @param focusField the field that received focus.
     */
    fun onFocusChange(focusField: CardFieldType)

    /**
     * Called when field validation status has changed from invalid or undefined to valid status.
     *
     * @param field the exact field whose validation status has changed
     */
    fun onFieldValidStatus(field: CardFieldType)

    /**
     * Called when field validation status has changed from valid or undefined to invalid status.
     *
     * @param field the exact whose validation status has changed
     * @param status the new validation status
     */
    fun onFieldInvalidStatus(field: CardFieldType, status: ValidationStatus.Invalid)

    /**
     * Called when the validation status of card input has changed. This occurs in result of
     * a validation status change of one or more child views.
     *
     * @param valid true if the card input is valid or false otherwise
     */
    fun onCardValidationChanged(valid: Boolean)

    /**
     * Called when the card is valid and complete.
     *
     * This event occurs when the last field character of field with known length
     * (number, expiry or CVV) is entered and the rest of the fields contain valid data.t
     */
    fun onCardInputComplete()
}