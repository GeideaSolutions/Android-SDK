package net.geidea.paymentsdk.ui.validation

import org.junit.Test
import kotlin.test.assertEquals

class FieldMaxLengthValidatorTest {

    private val validator = FieldMaxLengthValidator { dummyReason }

    @Test
    fun validate_withNull_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate(null))
    }

    @Test
    fun validate_withEmpty_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate(""))
    }

    @Test
    fun validate_with255CharsLong_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate("a".repeat(255)))
    }

    @Test
    fun validate_with256CharsLong_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(dummyReason), validator.validate("a".repeat(255 + 1)))
    }
}