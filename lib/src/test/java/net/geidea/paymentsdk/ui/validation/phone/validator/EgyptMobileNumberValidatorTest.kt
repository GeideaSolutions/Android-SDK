package net.geidea.paymentsdk.ui.validation.phone.validator

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.assertThat
import org.junit.Test

class EgyptMobileNumberValidatorTest {

    private val validator = EgyptMobileNumberValidator

    @Test
    fun validate_withNonDigits_returnsInvalid() {
        assertInvalid(validator.validate("00201a12345678"))
        assertInvalid(validator.validate("+201a12345678"))
        assertInvalid(validator.validate("201a12345678"))
        assertInvalid(validator.validate("01a12345678"))
    }

    @Test
    fun validate_withTooShort_returnsInvalid() {
        assertInvalid(validator.validate("0020101234567"))
        assertInvalid(validator.validate("+20101234567"))
        assertInvalid(validator.validate("20101234567"))
        assertInvalid(validator.validate("0101234567"))
    }

    @Test
    fun validate_withTooLong_returnsInvalid() {
        assertInvalid(validator.validate("002010123456789"))
        assertInvalid(validator.validate("+2010123456789"))
        assertInvalid(validator.validate("2010123456789"))
        assertInvalid(validator.validate("010123456789"))
    }

    @Test
    fun validate_withCorrect_returnsValid() {
        assertValid(validator.validate("00201012345678"))
        assertValid(validator.validate("+201012345678"))
        assertValid(validator.validate("201012345678"))
        assertValid(validator.validate("01012345678"))
    }

    private fun assertValid(validationStatus: ValidationStatus) {
        assertThat(validationStatus, `is`(instanceOf(ValidationStatus.Valid::class.java)))
    }

    private fun assertInvalid(validationStatus: ValidationStatus) {
        assertThat(validationStatus, `is`(instanceOf(ValidationStatus.Invalid::class.java)))
    }
}