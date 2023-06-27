package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.reason.UnacceptedCardBrand
import net.geidea.paymentsdk.ui.widget.card.CardBrandFilter
import org.junit.Test
import kotlin.test.assertEquals

class AcceptedCardBrandValidatorTest {

    val validator = AcceptedCardBrandValidator(cardBrandFilter = null)

    @Test
    fun validate_withNullFilter_acceptsSupportedBrands() {
        assertEquals(ValidationStatus.Valid, validator.validate(CardBrand.Visa.prefixes[0]))
        assertEquals(ValidationStatus.Valid, validator.validate(CardBrand.Mastercard.prefixes[0]))
        assertEquals(ValidationStatus.Valid, validator.validate(CardBrand.AmericanExpress.prefixes[0]))
        assertEquals(ValidationStatus.Valid, validator.validate(CardBrand.Mada.prefixes[0]))
    }

    @Test
    fun validate_withUnknownBrand_returnsUnacceptedCardBrand() {
        // No brand starts with "0"
        assertEquals(ValidationStatus.Invalid(UnacceptedCardBrand), validator.validate("0"))
    }

    @Test
    fun validate_withAcceptedNumber_returnsValid() {
        validator.cardBrandFilter = CardBrandFilter { it == CardBrand.Visa }
        assertEquals(ValidationStatus.Valid, validator.validate("4111 1111 1111 1111"))  // Visa
        assertEquals(ValidationStatus.Valid, validator.validate("4111111111111111"))  // Visa
    }

    @Test
    fun validate_withUnacceptedNumber_returnsUnacceptedCardBrand() {
        validator.cardBrandFilter = CardBrandFilter { it == CardBrand.Mastercard }
        assertEquals(ValidationStatus.Invalid(UnacceptedCardBrand), validator.validate("4111 1111 1111 1111"))  // Visa
        assertEquals(ValidationStatus.Invalid(UnacceptedCardBrand), validator.validate("4111111111111111"))  // Visa

    }
}