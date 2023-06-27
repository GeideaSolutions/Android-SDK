package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardNumberLength

/**
 * Validator that detects the card brand and checks if the card number length is valid for that
 * card brand.
 *
 * Possible invalidation reasons:
 * - [InvalidCardNumberLength]
 *
 * @see CardBrand.maxLength
 */
object CardNumberLengthValidator : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        val cardNumberNoSpaces: String = value?.replace(" ", "") ?: ""
        if (value.isNullOrEmpty()) {
            return ValidationStatus.Invalid(InvalidCardNumberLength)
        }

        val brand: CardBrand = CardBrand.fromCardNumberPrefix(cardNumberNoSpaces)
        return if (brand.matchesCardNumberLength(cardNumberNoSpaces.length)) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(InvalidCardNumberLength)
        }
    }
}

