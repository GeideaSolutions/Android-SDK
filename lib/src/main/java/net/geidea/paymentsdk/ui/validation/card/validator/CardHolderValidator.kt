package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.ui.validation.SimpleCharFilter
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardHolderCharacters
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardHolderLength

/**
 * Validator for the [card holder][net.geidea.paymentsdk.model.Card.cardHolderName] property.
 *
 * Possible invalidation reasons:
 * - [InvalidCardHolderLength]
 * - [InvalidCardHolderCharacters]
 */
object CardHolderValidator : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        return when {
            value.isNullOrEmpty() ->  ValidationStatus.Invalid(InvalidCardHolderLength)
            value.isBlank() -> ValidationStatus.Invalid(InvalidCardHolderCharacters)
            value.trim().length !in 3..255 -> ValidationStatus.Invalid(InvalidCardHolderLength)
            !value.all(CardHolderValidator::charFilterFun) -> ValidationStatus.Invalid(InvalidCardHolderCharacters)
            else -> ValidationStatus.Valid
        }
    }

    private fun charFilterFun(c: Char): Boolean = c.isLetter() || c == ' '

    val cardHolderCharFilter = SimpleCharFilter(CardHolderValidator::charFilterFun)
}