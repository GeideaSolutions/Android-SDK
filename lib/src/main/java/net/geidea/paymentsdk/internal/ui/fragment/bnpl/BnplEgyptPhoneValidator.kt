package net.geidea.paymentsdk.internal.ui.fragment.bnpl

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.phone.reason.InvalidPhoneNumber

@GeideaSdkInternal
internal object BnplEgyptPhoneValidator : Validator<String> {
    override fun validate(value: String?): ValidationStatus {
        return if (value != null
            && value.length == MAX_LENGTH
            && value.startsWith('1')
            && value.all { c -> c in '0'..'9' }
        ) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(InvalidPhoneNumber)
        }
    }

    const val MAX_LENGTH = 10
}