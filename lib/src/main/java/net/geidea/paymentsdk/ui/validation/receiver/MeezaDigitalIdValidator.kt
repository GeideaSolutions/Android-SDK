package net.geidea.paymentsdk.ui.validation.receiver

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.phone.reason.InvalidPhoneNumber

object MeezaDigitalIdValidator : Validator<String> {
    override fun validate(value: String?): ValidationStatus {
        return if (value != null
                && value.length == 9
                && value.all { character -> character.isDigit() }) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(InvalidPhoneNumber)
        }
    }
}