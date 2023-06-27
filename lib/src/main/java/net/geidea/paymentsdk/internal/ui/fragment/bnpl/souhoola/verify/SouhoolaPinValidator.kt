package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.verify

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.pin.reason.InvalidPin

internal class SouhoolaPinValidator(val lengthRange: IntRange = 1..10) : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        if (value.isNullOrBlank() || value.length !in lengthRange || value.any { char -> !char.isDigit() }) {
            return ValidationStatus.Invalid(InvalidPin)
        }

        return ValidationStatus.Valid
    }
}