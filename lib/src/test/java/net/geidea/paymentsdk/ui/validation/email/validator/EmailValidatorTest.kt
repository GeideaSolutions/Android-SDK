package net.geidea.paymentsdk.ui.validation.email.validator

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.email.reason.InvalidEmail
import org.junit.Test
import kotlin.test.assertEquals

class EmailValidatorTest {

    private val validator = EmailValidator

    @Test
    fun validate_withNull_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate(null))
    }

    @Test
    fun validate_withEmpty_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate(""))
    }

    @Test
    fun validate_withValid_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate("user@test.com"))
    }

    @Test
    fun validate_withInvalid_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidEmail), validator.validate("user@test..com"))
    }
}