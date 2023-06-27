package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCvvCharacters
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCvvLength
import net.geidea.paymentsdk.ui.widget.card.OnCardBrandChangedListener

/**
 * Validator for [CVV][net.geidea.paymentsdk.model.Card.cvv]. Checks if the string contains only
 * digits and if the length is equal to the expected length for the [cardBrand].
 * For [CardBrand.Unknown] a valid length is 3 or 4.
 * Once the card brand is recognized from card number a call to [onCardBrandChanged] must be done.
 *
 * Possible invalidation reasons:
 * - [InvalidCvvLength]
 * - [InvalidCvvCharacters]
 *
 * @see CardBrand
 * @see CardBrand.securityCodeLengthRange
 */
class CvvValidator(
    private var cardBrand: CardBrand = CardBrand.Unknown
) : Validator<String>, OnCardBrandChangedListener {

    override fun onCardBrandChanged(cardBrand: CardBrand) {
        this.cardBrand = cardBrand
    }

    override fun validate(value: String?): ValidationStatus {
        return when {
            value == null || value.length !in cardBrand.securityCodeLengthRange -> ValidationStatus.Invalid(InvalidCvvLength)
            !value.all(Char::isDigit) -> ValidationStatus.Invalid(InvalidCvvCharacters)
            else -> ValidationStatus.Valid
        }
    }
}

