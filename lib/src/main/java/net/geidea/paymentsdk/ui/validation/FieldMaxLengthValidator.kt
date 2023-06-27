package net.geidea.paymentsdk.ui.validation

import net.geidea.paymentsdk.ui.widget.FormEditText

/**
 * Validator that checks for the maximal allowed text field length of 255.
 *
 * @param reasonFun function that returns the concrete invalidation reason based on the invalid value.
 */
class FieldMaxLengthValidator(private val reasonFun: (String?) -> InvalidationReason) : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        return if (value.isNullOrEmpty() || value.length <= FormEditText.MAX_FIELD_LENGTH) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(reasonFun(value))
        }
    }
}