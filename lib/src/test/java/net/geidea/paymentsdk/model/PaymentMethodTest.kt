package net.geidea.paymentsdk.model

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class PaymentMethodTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    private val validCardHolder = "John Doe"
    private val validOwner = "John Doe"
    private val validCardNumber = "1234"
    private val validExpiry = ExpiryDate(12, 25)
    private val validCvv = "123"

    // All valid

    @Test
    fun build_withValidData_setsAllProperties() {
        val actual = PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setOwner(validOwner)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()

        assertEquals(validCardHolder, actual.cardHolderName)
        assertEquals(validCardNumber, actual.cardNumber)
        assertEquals(validOwner, actual.owner)
        assertEquals(validExpiry, actual.expiryDate)
        assertEquals(validCvv, actual.cvv)
    }

    // Card holder

    @Test
    fun build_withNotSetCardHolder_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing card holder")

        PaymentMethod.Builder()
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withNullCardHolder_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing card holder")

        PaymentMethod.Builder()
                .setCardHolderName(null)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withEmptyCardHolder_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid card holder name: must not be empty")

        PaymentMethod.Builder()
                .setCardHolderName("")
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_with255CharsCardHolder_succeeds() {
        val chars255 = StringBuilder()
        repeat(255) {
            chars255.append('c')
        }

        PaymentMethod.Builder()
                .setCardHolderName(chars255.toString())
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_with256CharsCardHolder_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid card holder name: exceeds max length of 255")

        val chars256 = StringBuilder()
        repeat(256) {
            chars256.append('c')
        }

        PaymentMethod.Builder()
                .setCardHolderName(chars256.toString())
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    // Card number

    @Test
    fun build_withNotSetCardNumber_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing card number")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withNullCardNumber_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing card number")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(null)
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withEmptyCardNumber_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid card number: must not be empty")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber("")
                .setExpiryDate(validExpiry)
                .setCvv(validCvv)
                .build()
    }

    // Expiry

    @Test
    fun build_withNotSetExpiryDate_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing expiry date")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withNullExpiryDate_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing expiry date")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(null)
                .setCvv(validCvv)
                .build()
    }

    // Expiry month

    @Test
    fun build_withNegativeExpiryMonth_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry month: must be 1..12")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(-1, 25))
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withZeroExpiryMonth_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry month: must be 1..12")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(0, 25))
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withTooBigExpiryMonth_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry month: must be 1..12")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(13, 25))
                .setCvv(validCvv)
                .build()
    }

    // Expiry year

    @Test
    fun build_withNegativeExpiryYear_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry year: must be 1..99")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(1, -1))
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withZeroExpiryYear_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry year: must be 1..99")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(1, 0))
                .setCvv(validCvv)
                .build()
    }

    @Test
    fun build_withTooBigExpiryYear_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid expiry year: must be 1..99")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(ExpiryDate(1, 100))
                .setCvv(validCvv)
                .build()
    }

    // Cvv

    @Test
    fun build_withNotSetCvv_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing CVV")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .build()
    }

    @Test
    fun build_withNullCvv_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing CVV")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv(null)
                .build()
    }

    @Test
    fun build_withEmptyCvv_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid CVV: must be 3 or 4 characters")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("")
                .build()
    }

    @Test
    fun build_with2DigitCvv_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid CVV: must be 3 or 4 characters")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("12")
                .build()
    }

    @Test
    fun build_with3DigitCvv_succeeds() {
        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("123")
                .build()
    }

    @Test
    fun build_withCvvContainingLetter_succeeds() {
        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("12a")
                .build()
    }

    @Test
    fun build_with4DigitCvv_succeeds() {
        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("1234")
                .build()
    }

    @Test
    fun build_with5DigitCvv_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid CVV: must be 3 or 4 characters")

        PaymentMethod.Builder()
                .setCardHolderName(validCardHolder)
                .setCardNumber(validCardNumber)
                .setExpiryDate(validExpiry)
                .setCvv("12345")
                .build()
    }
}