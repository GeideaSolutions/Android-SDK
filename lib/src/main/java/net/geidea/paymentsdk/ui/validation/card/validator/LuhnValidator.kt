package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.internal.util.Validations.luhnCheck
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidLuhnChecksum

/**
 * Validator perform Luhn checksum validation.
 *
 *
 */
object LuhnValidator : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        return if (!luhnCheck(value)) {
            ValidationStatus.Invalid(InvalidLuhnChecksum)
        } else {
            ValidationStatus.Valid
        }
    }
}

