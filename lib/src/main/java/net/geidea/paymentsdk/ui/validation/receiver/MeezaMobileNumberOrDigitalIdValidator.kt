package net.geidea.paymentsdk.ui.validation.receiver

import net.geidea.paymentsdk.ui.validation.CompositeValidator
import net.geidea.paymentsdk.ui.validation.CompositeValidator.Companion.anyOf
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.phone.validator.EgyptMobileNumberValidator

object MeezaMobileNumberOrDigitalIdValidator : Validator<String> {

    private val delegate: CompositeValidator<String> = anyOf(
            EgyptMobileNumberValidator,
            MeezaDigitalIdValidator
    )

    override fun validate(value: String?): ValidationStatus {
        return delegate.validate(value)
    }

    fun getNormalizedReceiverId(text: String?): String? {
        if (text == null) {
            return null
        }
        return when (ValidationStatus.Valid) {
            MeezaDigitalIdValidator.validate(text) -> {
                text
            }
            EgyptMobileNumberValidator.validate(text) -> {
                val numberWithoutPrefix = EgyptMobileNumberValidator.getNumberAfterPrefix(text)
                "00201$numberWithoutPrefix"
            }
            else -> {
                null
            }
        }
    }
}