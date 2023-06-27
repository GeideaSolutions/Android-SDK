package net.geidea.paymentsdk.ui.validation.email.validator

import net.geidea.paymentsdk.internal.util.Validations
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.email.reason.InvalidEmail

/**
 * Basic e-mail validator.
 *
 * Possible invalidation reasons:
 * [InvalidEmail].
 */
object EmailValidator : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        return if (value.isNullOrEmpty() || Validations.validateEmail(value)) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(InvalidEmail)
        }
    }
}