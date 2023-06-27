package net.geidea.paymentsdk.ui.validation.pin.validator

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.pin.reason.InvalidPin

class PinValidator(val digitCount: Int) : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        if (value.isNullOrBlank() || value.length != digitCount || value.any { char -> !char.isDigit() }) {
            return ValidationStatus.Invalid(InvalidPin)
        }

        return ValidationStatus.Valid
    }
}