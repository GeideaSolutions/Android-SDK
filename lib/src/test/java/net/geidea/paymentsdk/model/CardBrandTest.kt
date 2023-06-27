package net.geidea.paymentsdk.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CardBrandTest {
    
    //region fromCardNumberPrefix

    @Test
    fun fromCardNumberPrefix_withValidVisa_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("4222222222222")
        assertEquals(CardBrand.Visa, brand)
    }

    @Test
    fun fromCardNumberPrefix_withValidAmex_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("378282246310005")
        assertEquals(CardBrand.AmericanExpress, brand)
    }

    @Test
    fun fromCardNumberPrefix_withValidMastercard_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("5555555555554444")
        assertEquals(CardBrand.Mastercard, brand)
    }

    @Test
    fun fromCardNumberPrefix_withValidMada_returnsBrand() {
        var brand = CardBrand.fromCardNumberPrefix("5588480000000003")
        assertEquals(CardBrand.Mada, brand)

        brand = CardBrand.fromCardNumberPrefix("4464040000000007")
        assertEquals(CardBrand.Mada, brand)

        brand = CardBrand.fromCardNumberPrefix("558848")
        assertEquals(CardBrand.Mada, brand)

        brand = CardBrand.fromCardNumberPrefix("4008 61")
        assertEquals(CardBrand.Mada, brand)
    }

    @Test
    fun fromCardNumberPrefix_withValidMeeza_returnsBrand() {
        var brand = CardBrand.fromCardNumberPrefix("507803")
        assertEquals(CardBrand.Meeza, brand)

        brand = CardBrand.fromCardNumberPrefix("507808")
        assertEquals(CardBrand.Meeza, brand)
    }

    @Test
    fun fromCardNumberPrefix_withSpacesAtEnd_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("5588480000000003  ")
        assertEquals(CardBrand.Mada, brand)
    }

    @Test
    fun fromCardNumberPrefix_withSpacesInBetween_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("558848 0 00000 000 3")
        assertEquals(CardBrand.Mada, brand)
    }

    @Test
    fun fromCardNumberPrefix_withNewLineAtEnd_returnsBrand() {
        val brand = CardBrand.fromCardNumberPrefix("5588480000000003\n")
        assertEquals(CardBrand.Mada, brand)
    }

    @Test
    fun fromCardNumberPrefix_withEmpty_returnsUnknown() {
        val brand = CardBrand.fromCardNumberPrefix("")
        assertEquals(CardBrand.Unknown, brand)
    }

    @Test
    fun fromCardNumberPrefix_withBlank_returnsUnknown() {
        val brand = CardBrand.fromCardNumberPrefix("\n\t")
        assertEquals(CardBrand.Unknown, brand)
    }

    @Test
    fun fromCardNumberPrefix_withNonDefinedPrefixAndSpaces_returnsUnknown() {
        val brand = CardBrand.fromCardNumberPrefix("3 00")
        assertEquals(CardBrand.Unknown, brand)
    }

    @Test
    fun fromCardNumberPrefix_withNonDefinedPrefix_returnsUnknown() {
        val brand = CardBrand.fromCardNumberPrefix("999")
        assertEquals(CardBrand.Unknown, brand)
    }

    @Test
    fun fromCardNumberPrefix_withInvalidPrefix_returnsUnknown() {
        assertEquals(CardBrand.fromCardNumberPrefix(""), CardBrand.Unknown)
        assertEquals(CardBrand.fromCardNumberPrefix("abc"), CardBrand.Unknown)
        assertEquals(CardBrand.fromCardNumberPrefix(" "), CardBrand.Unknown)
        assertEquals(CardBrand.fromCardNumberPrefix("-"), CardBrand.Unknown)
    }

    @Test
    fun fromCardNumberPrefix_withSupportedBrandPrefix_returnsBrand() {
        assertEquals(CardBrand.fromCardNumberPrefix("4111111"), CardBrand.Visa)
        assertEquals(CardBrand.fromCardNumberPrefix("3411"), CardBrand.AmericanExpress)
        assertEquals(CardBrand.fromCardNumberPrefix("3711"), CardBrand.AmericanExpress)
        assertEquals(CardBrand.fromCardNumberPrefix("558848"), CardBrand.Mada)
        assertEquals(CardBrand.fromCardNumberPrefix("55"), CardBrand.Mastercard)
    }
    
    //endregion
    
    //region possibleBrands()

    @Test
    fun possibleBrands_withEmpty_returnsEmptySet() {
        assertEquals(emptySet<CardBrand>(), CardBrand.possibleBrands(""))
    }

    @Test
    fun possibleBrands_withNull_returnsEmptySet() {
        assertEquals(emptySet<CardBrand>(), CardBrand.possibleBrands(null))
    }

    @Test
    fun possibleBrands_withBlank_returnsEmptySet() {
        assertEquals(emptySet<CardBrand>(), CardBrand.possibleBrands("   "))
    }

    @Test
    fun possibleBrands_withMastercardPrefixSurroundedBySpaces_returnsMastercard() {
        // " 23 " is Visa prefix
        assertEquals(setOf(CardBrand.Mastercard), CardBrand.possibleBrands(" 23 "))
    }

    @Test
    fun possibleBrands_withAmexPrefixAndAdditionalDigit_returnsAmex() {
        // "34" is Amex prefix
        assertEquals(setOf(CardBrand.AmericanExpress), CardBrand.possibleBrands("341"))
    }

    @Test
    fun possibleBrands_withNonDefinedPrefix_returnsUnknown() {
        // No card with "999" prefix
        assertEquals(setOf(CardBrand.Unknown), CardBrand.possibleBrands("999"))
    }

    @Test
    fun possibleBrands_withCommonPrefixPart_returnsMultiple() {
        // With "55" start Mastercard and Mada
        val actualBrands = CardBrand.possibleBrands("55")
        assertEquals(setOf(CardBrand.Mada, CardBrand.Mastercard), actualBrands)
    }

    @Test
    fun possibleBrands_withMatchingForOneBrand_returnsSingle() {
        // With "22" start multiple types of one brand - Mastercard
        assertEquals(CardBrand.possibleBrands("22"), setOf(CardBrand.Mastercard))
    }

    @Test
    fun possibleBrands_withExactMatchButConflict_returnsSingleBestMatch() {
        // "400861" conflicts with Visa prefix "4" but is exact match for Mada
        val possibleBrands = CardBrand.possibleBrands("400861")
        assertTrue(possibleBrands.isNotEmpty())
        assertEquals(CardBrand.Mada, possibleBrands.first())
    }

    @Test
    fun possibleBrands_withCloseMatchButConflict_returnsBoth() {
        // "40086" conflicts with Visa prefix "4" but is NOT an exact match for Mada
        val possibleBrands = CardBrand.possibleBrands("40086")
        assertEquals(setOf(CardBrand.Mada, CardBrand.Visa), possibleBrands)
    }

    //endregion

    //region separateCardNumber()

    @Test
    fun separateCardNumber_forInvalidOrUnknownCardBrand_useDefaultGrouping() {
        var cardNumber = ""
        var result = CardBrand.Unknown.separateCardNumber(cardNumber)

        assertEquals(cardNumber, result)

        cardNumber = "0123456789012345"
        result = CardBrand.Unknown.separateCardNumber(cardNumber)

        assertEquals("0123 4567 8901 2345", result)

        cardNumber = "9!@#*_a343"
        result = CardBrand.Unknown.separateCardNumber(cardNumber)

        assertEquals("9!@# *_a3 43", result)
    }

    @Test
    fun separateCardNumber_withAmex_returnsSeparated() {
        assertEquals("3759", CardBrand.AmericanExpress.separateCardNumber("3759"))
        assertEquals("3759 876", CardBrand.AmericanExpress.separateCardNumber("3759876"))
        assertEquals("3759 876543 21", CardBrand.AmericanExpress.separateCardNumber("375987654321"))
        assertEquals("3759 876543 21001", CardBrand.AmericanExpress.separateCardNumber("375987654321001"))
    }

    @Test
    fun separateCardNumber_withVisa_returnsSeparated() {
        assertEquals("4", CardBrand.Visa.separateCardNumber("4"))
        assertEquals("4111", CardBrand.Visa.separateCardNumber("4111"))
        assertEquals("4111 1", CardBrand.Visa.separateCardNumber("41111"))
        assertEquals("4111 1111 1", CardBrand.Visa.separateCardNumber("411111111"))
        assertEquals("4111 1111 1111", CardBrand.Visa.separateCardNumber("411111111111"))
    }

    @Test
    fun separateCardNumber_withUnknown_returnsSeparatedBy4() {
        assertEquals("0000 0000 00", CardBrand.Unknown.separateCardNumber("0000000000"))
    }

    // endregion
}