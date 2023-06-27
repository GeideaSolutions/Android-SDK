package net.geidea.paymentsdk.flow.pay

import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.PaymentMethod
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.math.BigDecimal
import kotlin.test.assertEquals


class PaymentDataTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    companion object {
        private val fullAddress = Address(
                countryCode = "BGR",
                city = "Sofia",
                street = "Vitosha 89b",
                postCode = "1000"
        )

        private val allNullAddress = Address(
                countryCode = null,
                city = null,
                street = null,
                postCode = null
        )

        private val validExpiry = ExpiryDate(month = 12, year = 25)

        // Not a strictly valid card but enough to pass the client validation checks of the SDK
        private val validCard = PaymentMethod(
                cardNumber = "1234",
                cardHolderName = "John Doe",
                expiryDate = validExpiry,
                cvv = "123"
        )

        private val tooLong = 'a'.toString().repeat(255+1)

        private fun minimalBuilder() = PaymentData.Builder()
                .setAmount(BigDecimal("123.45"))
                .setCurrency("SAR")

        private fun fullBuilder() = PaymentData.Builder()
                .setAmount(BigDecimal("123.45"))
                .setCurrency("SAR")
                .setMerchantReferenceId("mrefId")
                .setCallbackUrl("https://callbackUrl.com")
                .setCustomerEmail("email@noreply.test")
                .setBillingAddress(fullAddress)
                .setShippingAddress(fullAddress)
                .setPaymentMethod(validCard)
    }

    //region Other

    @Test
    fun build_withValidData_setsAllProperties() {
        val actual = fullBuilder().build()

        assertEquals(BigDecimal("123.45"), actual.amount)
        assertEquals("SAR", actual.currency)
        assertEquals("mrefId", actual.merchantReferenceId)
        assertEquals("https://callbackUrl.com", actual.callbackUrl)
        assertEquals("email@noreply.test", actual.customerEmail)
        assertEquals(fullAddress, actual.billingAddress)
        assertEquals(fullAddress, actual.shippingAddress)
        assertEquals(validCard, actual.paymentMethod)
    }

    @Test
    fun build_withMinimalData_succeeds() {
        minimalBuilder().build()
    }

    //endregion

    //region Amount

    @Test
    fun build_withMissingAmount_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing amount")

        PaymentData.Builder()
                .setCurrency("SAR")
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_withNegativeAmount_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid amount")

        PaymentData.Builder()
                .setAmount(BigDecimal("-100.0"))
                .setCurrency("SAR")
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_withZeroAmount_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid amount")

        PaymentData.Builder()
                .setAmount(BigDecimal("0.0"))
                .setCurrency("SAR")
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_withMoreThan2FractionDigits_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid amount: must have at most 2 fractional digits")

        PaymentData.Builder()
                .setAmount(BigDecimal("100.123"))
                .setCurrency("SAR")
                .setPaymentMethod(validCard)
                .build()
    }

    //endregion

    //region Currency

    @Test
    fun build_withMissingCurrency_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Missing currency")

        PaymentData.Builder()
                .setAmount(BigDecimal("100.0"))
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_withEmptyCurrency_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid currency")

        PaymentData.Builder()
                .setAmount(BigDecimal("100.0"))
                .setCurrency("")
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_with2CharCurrency_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid currency")

        PaymentData.Builder()
                .setAmount(BigDecimal("100.0"))
                .setCurrency("SA")
                .setPaymentMethod(validCard)
                .build()
    }

    @Test
    fun build_with4CharCurrency_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid currency")

        PaymentData.Builder()
                .setAmount(BigDecimal("100.0"))
                .setCurrency("SABC")
                .setPaymentMethod(validCard)
                .build()
    }

    //endregion

    //region Email

    @Test
    fun build_withValidEmail_succeeds() {
        minimalBuilder()
                .setCustomerEmail("valid@email.com")
                .build()
    }

    @Test
    fun build_withEmptyEmail_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid email address")

        minimalBuilder()
                .setCustomerEmail("")
                .build()
    }

    @Test
    fun build_withInvalidEmail1_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid email address")

        minimalBuilder()
                .setCustomerEmail("invalidemail")
                .build()
    }

    //endregion

    //region MerchantReferenceId

    @Test
    fun build_withEmptyMerchantReferenceId_succeeds() {
        minimalBuilder()
                .setMerchantReferenceId("")
                .build()
    }

    //endregion

    //region Callback URL

    @Test
    fun build_withValidUrl_succeeds() {
        minimalBuilder()
                .setCallbackUrl("https://host.domain")
                .build()
    }

    @Test
    fun build_withHttpUrl_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid callback URL: must be valid https url")

        minimalBuilder()
                .setCallbackUrl("http://host.domain")
                .build()
    }

    @Test
    fun build_withEmptyUrl_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid callback URL: must not be empty")

        minimalBuilder()
                .setCallbackUrl("")
                .build()
    }

    //endregion

    //region Billing Address

    @Test
    fun build_validBillingAddress_succeeds() {
        minimalBuilder()
                .setBillingAddress(fullAddress)
                .build()
    }

    @Test
    fun build_withAllNullBillingAddressFields_setsNull() {
        val actual = minimalBuilder()
                .setBillingAddress(allNullAddress)
                .build()

        assertNull(actual.billingAddress)
    }

    @Test
    fun build_with2CharBillingCountryCode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid billing country code")

        minimalBuilder()
                .setBillingAddress(Address(countryCode = "SA"))
                .build()
    }

    @Test
    fun build_with4CharBillingCountryCode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid billing country code")

        minimalBuilder()
                .setBillingAddress(Address(countryCode = "SAAR"))
                .build()
    }

    @Test
    fun build_withTooLongBillingStreet_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid billing address")

        minimalBuilder()
                .setBillingAddress(Address(street = tooLong))
                .build()
    }

    @Test
    fun build_withTooLongBillingCity_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid billing address")

        minimalBuilder()
                .setBillingAddress(Address(city = tooLong))
                .build()
    }

    @Test
    fun build_withTooLongBillingPostcode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid billing address")

        minimalBuilder()
                .setBillingAddress(Address(postCode = tooLong))
                .build()
    }

    //endregion

    //region Shipping Address

    @Test
    fun build_validShippingAddress_succeeds() {
        minimalBuilder()
                .setShippingAddress(fullAddress)
                .build()
    }

    @Test
    fun build_withAllNullShippingAddressFields_setsNull() {
        val actual = minimalBuilder()
                .setShippingAddress(allNullAddress)
                .build()

        assertNull(actual.shippingAddress)
    }

    @Test
    fun build_with2CharShippingCountryCode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid shipping country code")

        minimalBuilder()
                .setShippingAddress(Address(countryCode = "SA"))
                .build()
    }

    @Test
    fun build_with4CharShippingCountryCode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid shipping country code")

        minimalBuilder()
                .setShippingAddress(Address(countryCode = "SAAR"))
                .build()
    }

    @Test
    fun build_withTooLongShippingStreet_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid shipping address")

        minimalBuilder()
                .setShippingAddress(Address(street = tooLong))
                .build()
    }

    @Test
    fun build_withTooLongShippingCity_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid shipping address")

        minimalBuilder()
                .setShippingAddress(Address(city = tooLong))
                .build()
    }

    @Test
    fun build_withTooLongShippingPostcode_throwsIAE() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid shipping address")

        minimalBuilder()
                .setShippingAddress(Address(postCode = tooLong))
                .build()
    }

    //endregion

    //region Payment method

    @Test
    fun build_withMissingPaymentMethod_succeeds() {
        PaymentData.Builder()
                .setAmount(BigDecimal("100.0"))
                .setCurrency("SAR")
                .build()
    }

    //endregion
}