package net.geidea.paymentsdk.flow.pay

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.*

class PaymentMethodsFilterTest {

    @MockK
    internal lateinit var mockMerchantConfig: MerchantConfigurationResponse

    @MockK
    internal lateinit var mockPaymentData: PaymentData

    @MockK
    internal lateinit var mockOrderItem: OrderItem

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockMerchantConfig.isSuccess } returns true
        every { mockMerchantConfig.isMeezaQrEnabled } returns false
        every { mockMerchantConfig.isValuBnplEnabled } returns false
        every { mockMerchantConfig.isShahryCnpBnplEnabled } returns false
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns false
        every { mockMerchantConfig.paymentMethods } returns emptySet()
        every { mockPaymentData.paymentOptions } returns null
        every { mockPaymentData.paymentMethod } returns null
        every { mockPaymentData.tokenId } returns null
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructor_withFailedMerchantConfig_throwsIAE() {
        // Given
        every { mockMerchantConfig.isSuccess } returns false

        // When
        PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)

        // Then: expected to throw
    }

    @Test
    fun createDefaultPaymentOptionsFromConfiguration_withAllMethodsEnabledInConfig_allShownAndInCorrectOrder() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("visa", "mastercard", "amex", "meeza", "mada")
        every { mockMerchantConfig.isMeezaQrEnabled } returns true
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockMerchantConfig.isShahryCnpBnplEnabled } returns true
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns true
        every { mockPaymentData.paymentOptions } returns null

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val defaultOptions: Set<PaymentOption> = filter.createDefaultPaymentOptions()

        // Then
        val expected = setOf(
            PaymentOption(paymentMethod = PaymentMethodDescriptor.Card(
                acceptedBrands = setOf(
                    CardBrand.Visa,
                    CardBrand.Mastercard,
                    CardBrand.AmericanExpress,
                    CardBrand.Meeza,
                    CardBrand.Mada,
                )
            )),
            PaymentOption(paymentMethod = PaymentMethodDescriptor.MeezaQr),
            PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.ValuInstallments),
            PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.ShahryInstallments),
            PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.SouhoolaInstallments),
        )
        assertEquals(expected, actual = defaultOptions)
    }

    // region filteredPaymentOptions

    @Test
    fun filteredPaymentOptions_whenNoPaymentOptions_defaultsToConfig() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("visa", "meeza")

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val filtered = filter.filteredPaymentOptions

        // Then
        val expected = setOf(
            PaymentOption(
                label = null,   // use default label
                paymentMethod = PaymentMethodDescriptor.Card(CardBrand.Visa, CardBrand.Meeza)
            )
        )
        assertEquals(expected, actual = filtered)
    }

    @Test
    fun filteredPaymentOptions_whenOptionContainsOnlyUnacceptableBrands_filtersItOut() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("mastercard")
        every { mockPaymentData.paymentOptions } returns PaymentOptions {
            option(PaymentMethodDescriptor.Card(CardBrand.Visa, CardBrand.Meeza))
            option(PaymentMethodDescriptor.Card(CardBrand.Mastercard))
        }

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val filtered = filter.filteredPaymentOptions

        // Then
        val expected = setOf(
            PaymentOption(
                label = null,   // use default label
                paymentMethod = PaymentMethodDescriptor.Card(CardBrand.Mastercard)
            )
        )
        assertEquals(expected, actual = filtered)
    }

    @Test
    fun filteredPaymentOptions_withTokenId_returnOnlyCard() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("visa")
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockPaymentData.tokenId } returns "dummyTokenId"

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val filtered = filter.filteredPaymentOptions

        // Then
        val expected = setOf(
            PaymentOption(paymentMethod = PaymentMethodDescriptor.Card(CardBrand.Visa))
        )
        assertEquals(expected, actual = filtered)
    }

    @Test
    fun filteredPaymentOptions_withMerchantProvidedCardData_returnsOnlyCard() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("visa")
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockPaymentData.paymentMethod } returns mockk()

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val filtered = filter.filteredPaymentOptions

        // Then
        val expected = setOf(
            PaymentOption(paymentMethod = PaymentMethodDescriptor.Card(CardBrand.Visa))
        )
        assertEquals(expected, actual = filtered)
    }

    // endregion

    @Test
    fun acceptedCardBrands_returnsIntersection() {
        // Given
        every { mockMerchantConfig.paymentMethods } returns setOf("visa", "mastercard", "amex")
        every { mockPaymentData.paymentOptions } returns PaymentOptions {
            option(PaymentMethodDescriptor.Card(CardBrand.Visa, CardBrand.Mastercard))
            option(PaymentMethodDescriptor.Card(CardBrand.Meeza))
            option(PaymentMethodDescriptor.MeezaQr)
        }

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)

        // Then
        val expected = setOf(CardBrand.Visa, CardBrand.Mastercard)
        assertEquals(expected, filter.acceptedCardBrands)
    }

    // region checkRequirements - order items

    @Test
    fun checkRequirements_withShahryAndNoOrderItems_returnsMissingOrderItems() {
        // Given
        every { mockMerchantConfig.isShahryCnpBnplEnabled } returns true
        every { mockPaymentData.amount } returns BigDecimal(501)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns null

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ShahryInstallments)

        // Then
        assertNotNull(result)
    }

    @Test
    fun checkRequirements_withSouhoolaAndNoOrderItems_returnsMissingOrderItems() {
        // Given
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns true
        every { mockMerchantConfig.souhoolaMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(501)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns null

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ShahryInstallments)

        // Then
        assertNotNull(result)
    }

    // endregion

    // region checkRequirements - currency

    @Test
    // This requirement will be most likely lifted in near future when multi-currency support is fully activated
    fun checkRequirements_withShahryAndNonEgyptCurrency_returnsInvalidCurrency() {
        // Given
        every { mockMerchantConfig.isShahryCnpBnplEnabled } returns true
        every { mockPaymentData.amount } returns BigDecimal(501)
        every { mockPaymentData.currency } returns "USD"

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ShahryInstallments)

        // Then
        assertNotNull(result)
    }

    @Test
    // This requirement will be most likely lifted in near future when multi-currency support is fully activated
    fun checkRequirements_withSouhoolaAndNonEgyptCurrency_returnsInvalidCurrency() {
        // Given
        every { mockMerchantConfig.isShahryCnpBnplEnabled } returns true
        every { mockMerchantConfig.souhoolaMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(501)
        every { mockPaymentData.currency } returns "USD"

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ShahryInstallments)

        // Then
        assertNotNull(result)
    }

    // endregion

    // region checkRequirements - minimum amount - valu

    @Test
    fun checkRequirements_withValuAndAmountEqualToMinimum_returnsNull() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockMerchantConfig.valUMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(500)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns listOf(mockOrderItem)

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertNull(result)
    }

    @Test
    fun checkRequirements_withValuAndAmountGreaterThanMinimum_returnsNull() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockMerchantConfig.valUMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(500.01)
        every { mockPaymentData.currency } returns "EGP"

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertNull(result)
    }

    @Test
    fun checkRequirements_withValuAndAmountLowerThanMinimum_returnsNotNull() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        every { mockMerchantConfig.valUMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(499.99)
        every { mockPaymentData.currency } returns "EGP"

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertNotNull(result)
    }

    // endregion

    // region checkRequirements - minimum amount - souhoola

    @Test
    fun checkRequirements_withSouhoolaAndAmountEqualToMinimum_returnsNull() {
        // Given
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns true
        every { mockMerchantConfig.souhoolaMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(500)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns listOf(mockOrderItem)

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.SouhoolaInstallments)

        // Then
        assertNull(result)
    }

    @Test
    fun checkRequirements_withSouhoolaAndAmountGreaterThanMinimum_returnsNull() {
        // Given
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns true
        every { mockMerchantConfig.souhoolaMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(500.01)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns listOf(mockOrderItem)

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.SouhoolaInstallments)

        // Then
        assertNull(result)
    }

    @Test
    fun checkRequirements_withSouhoolaAndAmountLowerThanMinimum_returnsNotNull() {
        // Given
        every { mockMerchantConfig.isSouhoolaCnpBnplEnabled } returns true
        every { mockMerchantConfig.souhoolaMinimumAmount } returns BigDecimal(500)
        every { mockPaymentData.amount } returns BigDecimal(499.99)
        every { mockPaymentData.currency } returns "EGP"
        every { mockPaymentData.orderItems } returns listOf(mockOrderItem)

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.checkRequirements(BnplPaymentMethodDescriptor.SouhoolaInstallments)

        // Then
        assertNotNull(result)
    }

    // endregion

    // region Should show Meeza QR?

    @Test
    fun shouldAllowMeezaQr_whenEnabledInConfig_returnsTrue() {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns true

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowMeezaQr()

        // Then
        assertTrue { result }
    }

    @Test
    fun shouldAllowMeezaQr_whenDisabledInConfig_returnsFalse() {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns false

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowMeezaQr()

        // Then
        assertFalse { result }
    }

    @Test
    fun shouldAllowMeezaQr_whenDisabledInConfigButListed_returnsFalse() {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns false
        every { mockPaymentData.paymentOptions } returns PaymentOptions { option(
            PaymentMethodDescriptor.MeezaQr) }

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowMeezaQr()

        // Then
        assertFalse { result }
    }

    // endregion

    // region Should show a BNPL method?

    @Test
    fun shouldAllowBnplMethod_whenEnabledInConfig_returnsTrue() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns true

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowBnplMethod(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertTrue { result }
    }

    @Test
    fun shouldAllowBnplMethod_whenEnabledInConfigAndNotListed_returnsFalse() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns true
        // Arbitrary options without valu
        every { mockPaymentData.paymentOptions } returns PaymentOptions { option(
            PaymentMethodDescriptor.Card()) }

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowBnplMethod(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertFalse { result }
    }

    @Test
    fun shouldAllowBnplMethod_whenDisabledInConfig_returnsFalse() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns false

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowBnplMethod(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertFalse { result }
    }

    @Test
    fun shouldAllowBnplMethod_whenDisabledInConfigButListed_returnsFalse() {
        // Given
        every { mockMerchantConfig.isValuBnplEnabled } returns false
        every { mockPaymentData.paymentOptions } returns PaymentOptions { option(
            BnplPaymentMethodDescriptor.ValuInstallments) }

        // When
        val filter = PaymentMethodsFilter(mockMerchantConfig, mockPaymentData)
        val result = filter.shouldAllowBnplMethod(BnplPaymentMethodDescriptor.ValuInstallments)

        // Then
        assertFalse { result }
    }

    // endregion
}