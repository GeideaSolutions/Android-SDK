package net.geidea.paymentsdk.ui.validation.phone.validator

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.phone.reason.InvalidPhoneNumber

object EgyptMobileNumberValidator : Validator<String> {
    private val prefixes = listOf(
            "00201",
            "+201",
            "201",
            "01",
    )

    /**
     * Expected length of the number after the code prefix
     */
    private const val localNumberLength = 9

    override fun validate(value: String?): ValidationStatus {
        if (value.isNullOrBlank() || value.any { char -> !char.isDigit() && char != '+' }) {
            return ValidationStatus.Invalid(InvalidPhoneNumber)
        }

        prefixes.forEach { prefix ->
            if (value.startsWith(prefix)) {
                val numberAfterPrefix = value.substring(prefix.length)
                if (numberAfterPrefix.length == localNumberLength) {
                    return ValidationStatus.Valid
                }
            }
        }

        return ValidationStatus.Invalid(InvalidPhoneNumber)
    }

    /**
     * Returns the 9-digit part of a valid mobile number after code prefix.
     */
    fun getNumberAfterPrefix(mobileNumber: String): String? {
        if (validate(mobileNumber) == ValidationStatus.Valid) {
            // Return the last 9 digits
            return mobileNumber.substring(mobileNumber.length - localNumberLength, mobileNumber.length)
        }

        return null
    }
}