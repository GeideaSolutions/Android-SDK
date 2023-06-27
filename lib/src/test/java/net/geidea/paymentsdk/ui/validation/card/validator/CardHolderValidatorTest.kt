package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardHolderCharacters
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardHolderLength
import org.junit.Test
import kotlin.test.assertEquals

class CardHolderValidatorTest {

    private val validator = CardHolderValidator

    @Test
    fun validate_withNull_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate(null))
    }

    @Test
    fun validate_withEmpty_returnsValid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate(""))
    }

    @Test
    fun validate_withBlank_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderCharacters), validator.validate(" ".repeat(3 + 1)))
    }

    @Test
    fun validate_withSpacesBeforeButShorterThan3_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate("  d"))
    }

    @Test
    fun validate_withSpacesAfterButShorterThan3_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate("d  "))
    }

    @Test
    fun validate_withSpacesBeforeAndAfterButShorterThan3_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate(" d "))
    }

    @Test
    fun validate_withSpaceBetweenAndLongExactly3_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate("a b"))
    }

    @Test
    fun validate_withSpaceBeforeBetweenAndAfter_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate(" a b "))
    }

    @Test
    fun validate_whenOutsideOfValidRange_returnsInvalid() {
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate("a".repeat(3 - 1)))
        assertEquals(ValidationStatus.Invalid(InvalidCardHolderLength), validator.validate("a".repeat(255 + 1)))
    }

    @Test
    fun validate_whenInsideOfValidRange_returnsValid() {
        assertEquals(ValidationStatus.Valid, validator.validate("a".repeat(3)))
        assertEquals(ValidationStatus.Valid, validator.validate("a".repeat(255)))
    }
}