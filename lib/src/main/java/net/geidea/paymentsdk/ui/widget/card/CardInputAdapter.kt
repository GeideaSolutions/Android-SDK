package net.geidea.paymentsdk.ui.widget.card

import net.geidea.paymentsdk.ui.validation.ValidationStatus

/**
 * Implementation of [CardInputListener] with empty method implementations.
 */
abstract class CardInputAdapter : CardInputListener {
    override fun onFocusChange(focusField: CardFieldType) {
        // Implementation is left for sub-classes
    }

    override fun onFieldValidStatus(field: CardFieldType) {
        // Implementation is left for sub-classes
    }

    override fun onFieldInvalidStatus(field: CardFieldType, status: ValidationStatus.Invalid) {
        // Implementation is left for sub-classes
    }

    override fun onCardValidationChanged(cardValid: Boolean) {
        // Implementation is left for sub-classes
    }

    override fun onCardInputComplete() {
        // Implementation is left for sub-classes
    }
}