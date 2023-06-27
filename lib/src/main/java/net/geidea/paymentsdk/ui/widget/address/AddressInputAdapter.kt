package net.geidea.paymentsdk.ui.widget.address

import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.ui.validation.ValidationStatus

/**
 * Implementation of [AddressInputListener] with empty method implementations.
 */
abstract class AddressInputAdapter : AddressInputListener {
    override fun onFocusChange(focusField: AddressFieldType) {
        // Implementation is left for sub-classes
    }

    override fun onFieldValidStatus(field: AddressFieldType) {
        // Implementation is left for sub-classes
    }

    override fun onFieldInvalidStatus(field: AddressFieldType, status: ValidationStatus.Invalid) {
        // Implementation is left for sub-classes
    }

    override fun onAddressValidationChanged(address: Address, valid: Boolean) {
        // Implementation is left for sub-classes
    }
}