package net.geidea.paymentsdk.ui.widget.address

import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.ui.validation.ValidationStatus

/**
 * Listener for address input events.
 */
interface AddressInputListener {

    /**
     * Called when a field has received focus.
     *
     * @param focusField the field that received focus.
     */
    fun onFocusChange(focusField: AddressFieldType)

    /**
     * Called when field validation status has changed from invalid or undefined to valid status.
     *
     * @param field the exact field whose validation status has changed
     */
    fun onFieldValidStatus(field: AddressFieldType)

    /**
     * Called when field validation status has changed from valid or undefined to invalid status.
     *
     * @param field the exact whose validation status has changed
     * @param status the new validation status
     */
    fun onFieldInvalidStatus(field: AddressFieldType, status: ValidationStatus.Invalid)

    /**
     * Called when the validation status of the entire address has changed. This occurs in result of
     * a validation status change of one or more child views.
     *
     * @param address the address value
     * @param valid the new validation status
     */
    fun onAddressValidationChanged(address: Address, valid: Boolean)
}