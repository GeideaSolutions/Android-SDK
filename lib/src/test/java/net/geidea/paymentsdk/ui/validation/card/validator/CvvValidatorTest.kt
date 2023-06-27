package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCvvCharacters
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCvvLength
import org.junit.Test
import kotlin.test.assertEquals

class CvvValidatorTest {

    val validator = CvvValidator()

    @Test
    fun validate_withNull_returnsValid() {
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate(null))
    }

    @Test
    fun validate_withEmpty_returnsValid() {
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate(""))
    }

    @Test
    fun validate_withInvalidChars_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCvvCharacters), validator.validate("abc"))
    }

    @Test
    fun validate_withValid_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate("123"))
        assertEquals(ValidationStatus.Valid, validator.validate("1234"))
    }

    @Test
    fun validate_withTooShort_returnsInvalid() {
        // By default expected length is 3 or 4
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate("12"))
    }

    @Test
    fun validate_withTooLong_returnsInvalid() {
        // By default expected length is 3 or 4
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate("12345"))
    }

    @Test
    fun validate_whenSwitchedToVisaAnd3Digits_returnsValid() {
        // Visa CVV is 3-digit
        validator.onCardBrandChanged(CardBrand.Visa)
        assertEquals(ValidationStatus.Valid, validator.validate("123"))
    }

    @Test
    fun validate_whenSwitchedToAnd4Digits_returnsInvalid() {
        // Visa CVV is 3-digit
        validator.onCardBrandChanged(CardBrand.Visa)
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate("1234"))
    }

    @Test
    fun validate_whenSwitchedToAmexAnd4Digits_returnsValid() {
        // By default expected length is 3 or 4, now make it 4
        validator.onCardBrandChanged(CardBrand.AmericanExpress)
        assertEquals(ValidationStatus.Valid, validator.validate("1234"))
    }

    @Test
    fun validate_whenSwitchedToAmexAnd2Digits_returnsInvalid() {
        // By default expected length is 3 or 4, now make it 2
        validator.onCardBrandChanged(CardBrand.AmericanExpress)
        assertEquals(ValidationStatus.Invalid(InvalidCvvLength), validator.validate("12"))
    }
}