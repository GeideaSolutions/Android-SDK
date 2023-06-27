package net.geidea.paymentsdk.internal.util

import net.geidea.paymentsdk.internal.util.Validations.luhnCheck
import net.geidea.paymentsdk.internal.util.Validations.simpleVerifyEmail
import net.geidea.paymentsdk.internal.util.Validations.validateHttpsUrl
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationsTest {

    @Test
    fun simpleVerifyEmail_withValid_returnsTrue() {
        assertTrue(simpleVerifyEmail("user.second.name@test.com"))
        assertTrue(simpleVerifyEmail("user.secondName@test.com"))
        assertTrue(simpleVerifyEmail("o'conner@test.com"))
        assertTrue(simpleVerifyEmail("user@test.com"))
        assertTrue(simpleVerifyEmail("user@test.subdomain.cc"))
        assertTrue(simpleVerifyEmail("\"user\"@test.com"))
        assertTrue(simpleVerifyEmail("\"user[test]\"@test.com"))
    }

    @Test
    fun simpleVerifyEmail_withInvalid_returnsFalse() {
        assertFalse(simpleVerifyEmail("user@test..com"))
        assertFalse(simpleVerifyEmail("user@[test].com"))
        assertFalse(simpleVerifyEmail("[use]r@test.com"))
        assertFalse(simpleVerifyEmail("user@@test.com"))
        assertFalse(simpleVerifyEmail("user@test..com"))
        assertFalse(simpleVerifyEmail("user @test.com"))
        assertFalse(simpleVerifyEmail("user..@test.com"))
        assertFalse(simpleVerifyEmail("user.second.@test.com"))
        assertFalse(simpleVerifyEmail("test\"user@test.com"))
    }

    @Test
    fun isValidHttpsUrl_withValid_returnsTrue() {
        assertTrue(validateHttpsUrl("https://host.domain"))
        assertFalse(validateHttpsUrl("http://host.domain"))
    }

    @Test
    fun isValidHttpsUrl_withInvalid_returnsFalse() {
        assertFalse(validateHttpsUrl(""))
        assertFalse(validateHttpsUrl("aaa"))
        assertFalse(validateHttpsUrl("://"))
    }

    // Luhn check

    @Test
    fun luhnCheck_withNullInput_returnsFalse() {
        assertFalse(luhnCheck(null))
    }

    @Test
    fun luhnCheck_withEmptyInput_returnsFalse() {
        assertFalse(luhnCheck(""))
    }

    @Test
    fun luhnCheck_withInvalidInput_returnsFalse() {
        assertFalse(luhnCheck("Hello world"))
        assertFalse(luhnCheck("4712255699706126"))
        assertFalse(luhnCheck("4712255699706117"))
        assertFalse(luhnCheck("4556233$$$$@#!*($&"))
        assertFalse(luhnCheck("-=@\$_+)$@)"))
    }

    @Test
    fun luhnCheck_withValidInput_returnsTrue() {
        // Visa
        assertTrue(luhnCheck(VISA1))
        assertTrue(luhnCheck(VISA2))
        assertTrue(luhnCheck(VISA3))

        // Visa Electron
        assertTrue(luhnCheck(VISA_ELECTRON1))
        assertTrue(luhnCheck(VISA_ELECTRON2))
        assertTrue(luhnCheck(VISA_ELECTRON3))

        // Mastercard
        assertTrue(luhnCheck(MASTERCARD1))
        assertTrue(luhnCheck(MASTERCARD2))
        assertTrue(luhnCheck(MASTERCARD3))

        // Maestro
        assertTrue(luhnCheck(MAESTRO1))
        assertTrue(luhnCheck(MAESTRO2))
        assertTrue(luhnCheck(MAESTRO3))

        // Amex
        assertTrue(luhnCheck(AMEX1))
        assertTrue(luhnCheck(AMEX2))
        assertTrue(luhnCheck(AMEX3))

        // Discover
        assertTrue(luhnCheck(DISCOVER1))
        assertTrue(luhnCheck(DISCOVER2))
        assertTrue(luhnCheck(DISCOVER3))

        // JCB
        assertTrue(luhnCheck(JCB1))
        assertTrue(luhnCheck(JCB2))
        assertTrue(luhnCheck(JCB3))

        // DinersClub North America
        assertTrue(luhnCheck(DINERSCLUB_NORTH_AMERICA1))
        assertTrue(luhnCheck(DINERSCLUB_NORTH_AMERICA2))
        assertTrue(luhnCheck(DINERSCLUB_NORTH_AMERICA3))

        // DinersClub Carte Blanche
        assertTrue(luhnCheck(DINERSCLUB_CARTE_BLANCHE1))
        assertTrue(luhnCheck(DINERSCLUB_CARTE_BLANCHE2))
        assertTrue(luhnCheck(DINERSCLUB_CARTE_BLANCHE3))

        // DinersClub International
        assertTrue(luhnCheck(DINERSCLUB_INTERNATIONAL1))
        assertTrue(luhnCheck(DINERSCLUB_INTERNATIONAL2))
        assertTrue(luhnCheck(DINERSCLUB_INTERNATIONAL3))

        // Instapayment
        assertTrue(luhnCheck(INSTAPAYMENT1))
        assertTrue(luhnCheck(INSTAPAYMENT2))
        assertTrue(luhnCheck(INSTAPAYMENT3))

        // MADA
        assertTrue(luhnCheck(MADA1))
        assertTrue(luhnCheck(MADA2))
    }
}