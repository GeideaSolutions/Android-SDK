package net.geidea.paymentsdk.flow.pay

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.MerchantsService
import net.geidea.paymentsdk.internal.util.Logger
import net.geidea.paymentsdk.internal.util.NativeTextFormatter
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import net.geidea.paymentsdk.util.TestCoroutineRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class PaymentViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    internal lateinit var mockMerchantsService: MerchantsService

    @MockK
    internal lateinit var mockCancellationService: CancellationService

    @MockK
    internal lateinit var mockPaymentMethodsFilter: PaymentMethodsFilter

    @MockK
    internal lateinit var mockNativeTextFormatter: NativeTextFormatter

    @MockK
    internal lateinit var mockPaymentData: PaymentData

    @MockK
    internal lateinit var mockMerchantConfigurationResponse: MerchantConfigurationResponse

    // SUT
    private lateinit var paymentViewModel: PaymentViewModel

    @Before
    fun setUp() {
        mockkObject(GeideaPaymentSdk)
        mockkObject(Logger)

        MockKAnnotations.init(this)

        // Default responses to simulate the happy path

        every { GeideaPaymentSdk.merchantKey }.returns("mk")

        with(mockNativeTextFormatter) {
            every { format(any()) } returns "dummyText"
        }

        with(mockPaymentData) {
            every { paymentMethod } returns null
            every { tokenId } returns null
            every { paymentOptions } returns null
        }

        with(mockMerchantsService) {
            coEvery { getMerchantConfiguration(eq("mk")) } returns mockMerchantConfigurationResponse
            every { cachedMerchantConfiguration } returns mockMerchantConfigurationResponse
        }

        with(mockMerchantConfigurationResponse) {
            every { isSuccess } returns true
        }

        with(Logger) {
            every { logv(any()) }.just(Runs)
            every { logd(any()) }.just(Runs)
            every { logi(any()) }.just(Runs)
            every { logw(any()) }.just(Runs)
            every { loge(any()) }.just(Runs)
        }

        paymentViewModel = createViewModel(paymentData = mockPaymentData)

        // Simulate that a specific payment method flow is started
        paymentViewModel.onPaymentMethodSelected(PaymentMethodDescriptor.Card(), isBnplDownPayment = false)
        /*paymentViewModel.onPaymentMethodSelected(PaymentMethodDescriptor.Card(), isBnplDownPayment = true)
        paymentViewModel.bnplPaymentMethod = BnplPaymentMethodDescriptor.ValuInstallments*/
    }

    private fun createViewModel(paymentData: PaymentData): PaymentViewModel {
        return PaymentViewModel(
            mockMerchantsService,
            mockCancellationService,
            { mockPaymentMethodsFilter },
            mockNativeTextFormatter,
            paymentData
        )
    }

    @Test
    fun shouldShowReceiptScreen_withSuccessAndEnabledInConfig_returnsCorrect() {
        val mockSuccessResult = mockk<GeideaResult.Success<Order>>()
        every { mockMerchantConfigurationResponse.isTransactionReceiptEnabled } returns true

        every { mockPaymentData.showReceipt } returns true
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))

        every { mockPaymentData.showReceipt } returns false
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))

        every { mockPaymentData.showReceipt } returns null
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))
    }

    @Test
    fun shouldShowReceiptScreen_withSuccessAndDisabledInConfig_returnsCorrect() {
        val mockSuccessResult = mockk<GeideaResult.Success<Order>>()
        every { mockMerchantConfigurationResponse.isTransactionReceiptEnabled } returns false

        every { mockPaymentData.showReceipt } returns true
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))

        every { mockPaymentData.showReceipt } returns false
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))

        every { mockPaymentData.showReceipt } returns null
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockSuccessResult))
    }

    @Test
    fun shouldShowReceiptScreen_withErrorAndEnabledInConfig_returnsCorrect() {
        val mockErrorResult = mockk<GeideaResult.Error>()
        every { mockMerchantConfigurationResponse.isTransactionReceiptEnabled } returns true

        every { mockPaymentData.showReceipt } returns true
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))

        every { mockPaymentData.showReceipt } returns false
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))

        every { mockPaymentData.showReceipt } returns null
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))
    }

    @Test
    fun shouldShowReceiptScreen_withErrorAndDisabledInConfig_returnsCorrect() {
        val mockErrorResult = mockk<GeideaResult.Error>()
        every { mockMerchantConfigurationResponse.isTransactionReceiptEnabled } returns false

        every { mockPaymentData.showReceipt } returns true
        assertTrue(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))

        every { mockPaymentData.showReceipt } returns false
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))

        every { mockPaymentData.showReceipt } returns null
        assertFalse(paymentViewModel.shouldShowReceiptScreen(mockErrorResult))
    }
}