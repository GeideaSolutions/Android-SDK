//package net.geidea.paymentsdk.flow.pay
//
//import android.app.Application
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import io.mockk.*
//import io.mockk.impl.annotations.MockK
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import net.geidea.paymentsdk.GeideaPaymentSdk
//import net.geidea.paymentsdk.flow.GeideaContract
//import net.geidea.paymentsdk.flow.GeideaResult
//import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
//import net.geidea.paymentsdk.internal.service.AuthenticationV3Service
//import net.geidea.paymentsdk.internal.service.CancellationService
//import net.geidea.paymentsdk.internal.service.PaymentService
//import net.geidea.paymentsdk.internal.ui.fragment.base.Snack
//import net.geidea.paymentsdk.internal.util.Event
//import net.geidea.paymentsdk.internal.util.Logger
//import net.geidea.paymentsdk.internal.util.NativeText
//import net.geidea.paymentsdk.model.*
//import net.geidea.paymentsdk.util.TestCoroutineRule
//import org.junit.Assert.assertNull
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.rules.TestRule
//import java.math.BigDecimal
//import kotlin.test.assertEquals
//
//@ExperimentalCoroutinesApi
//class CardPaymentViewModelTest {
//
//    @get:Rule
//    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val testCoroutineRule = TestCoroutineRule()
//
//    @MockK
//    internal lateinit var mockAuthenticationV1Service: AuthenticationV1Service
//
//    @MockK
//    internal lateinit var mockAuthenticationV3Service: AuthenticationV3Service
//
//    @MockK
//    internal lateinit var mockPaymentService: PaymentService
//
//    @MockK
//    internal lateinit var mockCancellationService: CancellationService
//
//    @MockK
//    internal lateinit var mockMerchantConfig: MerchantConfigurationResponse
//
//    @MockK
//    internal lateinit var mockAuthResponse: AuthenticationResponse
//
//    @MockK
//    internal lateinit var mockInitAuthResponse: InitiateAuthenticationResponse
//
//    @MockK(relaxed = true)
//    internal lateinit var mockOrderResponse: OrderResponse
//
//    @MockK
//    internal lateinit var mockApplication: Application
//
//    @MockK
//    internal lateinit var mockAddress: Address
//
//    @Before
//    fun setUp() {
//        mockkObject(GeideaPaymentSdk)
//        mockkObject(Logger)
//
//        MockKAnnotations.init(this)
//
//        every { mockApplication.getString(any(), any()) } returns "dummyResString"
//
//        // MerchantConfig
//        every { mockMerchantConfig.useMpgsApiV60 }.returns(false)
//
//        // Address
//        every { mockAddress.countryCode }.returns("dummyCountryCode")
//        every { mockAddress.city }.returns("dummyCity")
//        every { mockAddress.postCode }.returns("dummyPostCode")
//        every { mockAddress.street }.returns("dummyStreet")
//
//        // AuthenticationResponse
//        every { mockAuthResponse.orderId }.returns("dummyOrderId")
//        every { mockAuthResponse.htmlBodyContent }.returns("dummyHtml")
//        every { mockAuthResponse.threeDSecureId }.returns("dummyThreeDSecureId")
//        every { mockAuthResponse.language }.returns("dummyLanguageCode")
//
//        every { Logger.logv(any()) }.just(Runs)
//        every { Logger.logd(any()) }.just(Runs)
//        every { Logger.logi(any()) }.just(Runs)
//        every { Logger.logw(any()) }.just(Runs)
//        every { Logger.loge(any()) }.just(Runs)
//    }
//
//    @Test
//    fun init_withSdkProvidedPaymentData_callsAuthenticateAndTransitionsToRedirected() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockAuthResponse.isSuccess }.returns(true)
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//
//        // When
//        val mockPaymentMethod = createMockPaymentMethod()
//        val mockPaymentData = createMockPaymentData(
//                billingAddress = mockAddress,
//                shippingAddress = mockAddress,
//                paymentMethod = mockPaymentMethod,
//        )
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        advanceUntilIdle()
//
//        // Then
//        viewModel.stateLiveData.assertEmitted(CardPaymentViewModel.State.Redirected("dummyHtml"))
//
//        coVerify {
//            mockAuthenticationV1Service.postAuthenticate(
//                    match {
//                        it.orderId == null
//                                && it.amount == BigDecimal("233.45")
//                                && it.currency == "SAR"
//                                && it.paymentOperation == PaymentOperation.PAY
//                                && it.merchantReferenceId == "dummyMerchantReferenceId"
//                                && it.callbackUrl == "dummyCallbackUrl"
//                                && it.customerEmail == "dummyCustomerEmail"
//                                && it.returnUrl == CardPaymentViewModel.RETURN_URL
//                                && it.billingAddress == mockAddress
//                                && it.shippingAddress == mockAddress
//                                && it.paymentMethod == mockPaymentMethod
//                    }
//            )
//        }
//    }
//
//    @Test
//    fun init_withNullPaymentMethod_transitionsToPaymentForm() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockAuthResponse.isSuccess }.returns(true)
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//
//        // When
//        val mockPaymentData = createMockPaymentData(paymentMethod = null)
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        advanceUntilIdle()
//
//        // Then
//        viewModel.stateLiveData.assertEmitted(CardPaymentViewModel.State.PaymentForm)
//    }
//
//    @Test
//    fun init_withSdkProvidedPaymentDataAndError300onAuthenticate_transitionsToPaymentFormAndShowsSnackbar() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(false)
//
//        val mockAuth600Response = mockk<AuthenticationResponse>().apply {
//            every { orderId }.returns(null)
//            every { isSuccess }.returns(false)
//            every { responseCode }.returns("300")
//            every { responseMessage }.returns("Dummy response message")
//            every { detailedResponseCode }.returns("Dummy detailed response code")
//            every { detailedResponseMessage }.returns("Dummy detailed response message")
//            every { language }.returns("Dummy language code")
//        }
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuth600Response
//        }
//
//        // When
//        val mockPaymentData = createMockPaymentData(paymentMethod = null)
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        // Mocked load Merchant config call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//        val snackObserver = viewModel.snackbarLiveData.test()
//        val clearCardObserver = viewModel.clearFormLiveData.test()
//
//        advanceUntilIdle()
//
//        val mockPaymentMethod = createMockPaymentMethod()
//        viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = "mock@email.com",
//                billingAddress = mockAddress,
//                shippingAddress = mockAddress,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        // Then
//        val expectedHistory = arrayOf(
//                CardPaymentViewModel.State.PaymentForm,
//                CardPaymentViewModel.State.Processing,      // Before Auth call
//                CardPaymentViewModel.State.PaymentForm
//        )
//        stateObserver.assertValueHistory(*expectedHistory)
//
//        snackObserver.assertValue { event ->
//            val snack: Snack = event.peekContent()
//            snack.title == NativeText.Plain("Dummy detailed response code") && snack.message != null
//        }
//
//        clearCardObserver.assertValue(Event(Unit))
//    }
//
//    @Test
//    fun getPrePopulatedCountryCode_withPaymentDataCountry_returnsIt() {
//        // Given
//        val mockMerchantConfig = mockk<MerchantConfigurationResponse>()
//        with(mockMerchantConfig) {
//            every { countries } returns setOf(supportedCountry1, supportedCountry2)
//            every { merchantCountryTwoLetterCode } returns "IT"
//        }
//
//        val mockPaymentData = createMockPaymentData()
//        val viewModel = createPaymentViewModel(mockPaymentData)
//
//        // When
//        val countryCode = viewModel.getPrePopulatedCountryCode(Address(countryCode = "SAU"), mockMerchantConfig)
//
//        // Then
//        assertEquals("SAU", countryCode)
//    }
//
//    @Test
//    fun getPrePopulatedCountryCode_withoutPaymentDataCountry_returnsDefault() {
//        // Given
//        val mockMerchantConfig = mockk<MerchantConfigurationResponse>()
//        with(mockMerchantConfig) {
//            every { countries } returns setOf(supportedCountry1, supportedCountry2)
//            every { merchantCountryTwoLetterCode } returns "US"
//        }
//
//        val mockPaymentData = createMockPaymentData()
//        val viewModel = createPaymentViewModel(mockPaymentData)
//
//        // When
//        val countryCode = viewModel.getPrePopulatedCountryCode(Address(), mockMerchantConfig)
//
//        // Then
//        assertEquals("USA", countryCode)
//    }
//
//    @Test
//    fun getPrePopulatedCountryCode_whenPaymentDataCountryIsUnsupported_returnsDefault() {
//        // Given
//        val mockMerchantConfig = mockk<MerchantConfigurationResponse>()
//        with(mockMerchantConfig) {
//            every { countries } returns setOf(supportedCountry2, unsupportedCountry)
//            every { merchantCountryTwoLetterCode } returns "US"
//        }
//
//        val mockPaymentData = createMockPaymentData()
//        val viewModel = createPaymentViewModel(mockPaymentData)
//
//        // When
//        val countryCode = viewModel.getPrePopulatedCountryCode(Address(countryCode = "ITA"), mockMerchantConfig)
//
//        // Then
//        assertEquals("USA", countryCode)
//    }
//
//    // End-to-end tests: from creation of the ViewModel to emitting a terminal state.
//
//    @Test
//    fun end2end_withMerchantProvidedPaymentData_happyPath() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(true)
//        every { mockAuthResponse.isSuccess }.returns(true)
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//        coEvery { mockPaymentService.postPay(any()) }.coAnswers {
//            delay(100)
//            mockOrderResponse
//        }
//
//        // When
//        val mockPaymentMethod = createMockPaymentMethod()
//        val mockPaymentData = createMockPaymentData(paymentMethod = mockPaymentMethod)
//        val viewModel = createPaymentViewModel(mockPaymentData)
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        viewModel.onReturnUrl(CardPaymentViewModel.RETURN_URL + "?code=000")
//        advanceUntilIdle()
//
//        // Then
//        val expectedHistory = arrayOf(
//                CardPaymentViewModel.State.Processing,      // Before Auth call
//                CardPaymentViewModel.State.Redirected(      // After Auth call
//                        htmlBodyContent = "dummyHtml"
//                ),
//                CardPaymentViewModel.State.Processing,      // Before Pay call
//                CardPaymentViewModel.State.Finished(        // After Pay call
//                        GeideaResult.Success(mockOrderResponse.order!!)
//                )
//        )
//        stateObserver.assertValueHistory(*expectedHistory)
//
//        coVerify {
//            mockPaymentService.postPay(
//                    match {
//                        it.orderId == "dummyOrderId"
//                                && it.amount == BigDecimal("233.45")
//                                && it.currency == "SAR"
//                                && it.threeDSecureId == "dummyThreeDSecureId"
//                                && it.paymentMethod == mockPaymentMethod
//                    }
//            )
//        }
//    }
//
//    @Test
//    fun end2end_withSdkProvidedPaymentData_happyPath() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(true)
//        every { mockAuthResponse.isSuccess }.returns(true)
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//        coEvery { mockPaymentService.postPay(any()) }.coAnswers {
//            delay(100)
//            mockOrderResponse
//        }
//
//        // When
//        val viewModel = createPaymentViewModel(createMockPaymentData())
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        val mockPaymentMethod = createMockPaymentMethod()
//         viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = "mock@email.com",
//                billingAddress = mockAddress,
//                shippingAddress = mockAddress,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        viewModel.onReturnUrl(CardPaymentViewModel.RETURN_URL + "?code=000")
//        // Mocked pay call now
//        advanceUntilIdle()
//
//        // Then
//        val expectedHistory = arrayOf(
//                CardPaymentViewModel.State.PaymentForm,
//                CardPaymentViewModel.State.Processing,      // Before Auth call
//                CardPaymentViewModel.State.Redirected(      // After Auth call
//                        htmlBodyContent = "dummyHtml"
//                ),
//                CardPaymentViewModel.State.Processing,      // Before Pay call
//                CardPaymentViewModel.State.Finished(        // After Pay call
//                        GeideaResult.Success(mockOrderResponse.order!!)
//                )
//        )
//        stateObserver.assertValueHistory(*expectedHistory)
//
//        coVerify {
//            mockPaymentService.postPay(
//                    match {
//                        it.orderId == "dummyOrderId"
//                                && it.amount == BigDecimal("233.45")
//                                && it.currency == "SAR"
//                                && it.threeDSecureId == "dummyThreeDSecureId"
//                                && it.paymentMethod == mockPaymentMethod
//                    }
//            )
//        }
//    }
//
//    @Test
//    fun end2end_withSdkProvidedPaymentDataAndError600onAuthenticate_finishesWithError() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(false)
//
//        val mockAuth600Response = mockk<AuthenticationResponse>().apply {
//            every { orderId }.returns(null)
//            every { isSuccess }.returns(false)
//            every { responseCode }.returns("600")
//            every { responseMessage }.returns("Dummy response message")
//            every { detailedResponseCode }.returns("Dummy detailed response code")
//            every { detailedResponseMessage }.returns("Dummy detailed response message")
//            every { language }.returns("dummyLanguageCode")
//        }
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuth600Response
//        }
//
//        // When
//        val viewModel = createPaymentViewModel(createMockPaymentData())
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        val mockPaymentMethod = createMockPaymentMethod()
//        viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = null,
//                billingAddress = null,
//                shippingAddress = null,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        // Then
//        stateObserver.assertValue(CardPaymentViewModel.State.Finished(
//                GeideaResult.NetworkError(orderId = null, response = mockAuth600Response)
//        ))
//    }
//
//    @Test
//    fun end2end_withSdkProvidedPaymentDataAndError300onAuthenticate_finishesWithError() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(false)
//
//        val mockAuth600Response = mockk<AuthenticationResponse>().apply {
//            every { orderId }.returns(null)
//            every { isSuccess }.returns(false)
//            every { responseCode }.returns("600")
//            every { responseMessage }.returns("Dummy response message")
//            every { detailedResponseCode }.returns("Dummy detailed response code")
//            every { detailedResponseMessage }.returns("Dummy detailed response message")
//            every { language }.returns("dummyLanguageCode")
//        }
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuth600Response
//        }
//
//        // When
//        val viewModel = createPaymentViewModel(createMockPaymentData())
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        val mockPaymentMethod = createMockPaymentMethod()
//        viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = null,
//                billingAddress = null,
//                shippingAddress = null,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        // Then
//        stateObserver.assertValue(CardPaymentViewModel.State.Finished(
//                GeideaResult.NetworkError(orderId = null, response = mockAuth600Response)
//        ))
//    }
//
//    @Test
//    fun end2end_withMerchantProvidedPaymentDataAndError300OnAuthenticate_finishesWithError() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockOrderResponse.isSuccess }.returns(false)
//
//        val mockAuth600Response = mockk<AuthenticationResponse>().apply {
//            every { orderId }.returns(null)
//            every { isSuccess }.returns(false)
//            every { responseCode }.returns("300")
//            every { responseMessage }.returns("Dummy response message")
//            every { detailedResponseCode }.returns("Dummy detailed response code")
//            every { detailedResponseMessage }.returns("Dummy detailed response message")
//            every { language }.returns("dummyLanguageCode")
//        }
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockAuth600Response
//        }
//
//        // When
//        val mockPaymentMethod = createMockPaymentMethod()
//        val mockPaymentData = createMockPaymentData(paymentMethod = mockPaymentMethod)
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        // Mocked load MerchantConfig call now
//        // Mocked authenticate call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        // Then
//        stateObserver.assertValue(CardPaymentViewModel.State.Finished(
//                GeideaResult.NetworkError(orderId = null, response = mockAuth600Response)
//        ))
//    }
//
//    @Test
//    fun end2end_whenCancelledBeforeOrderIdIsSet_finishesWithCancelled() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//
//        // When
//        val mockPaymentData = createMockPaymentData()
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        assertNull(viewModel.orderId)
//        // Cancel before the authenticate call, viewModel.orderId will be null
//        viewModel.cancelAndFinish()
//
//        // Then
//        coVerify { mockCancellationService wasNot Called }
//        stateObserver.assertValue(CardPaymentViewModel.State.Finished(
//                GeideaResult.Cancelled(
//                        responseCode = "010",
//                        responseMessage = "Cancelled",
//                        detailedResponseCode = "001",
//                        detailedResponseMessage = "Cancelled by user",
//                        orderId = null
//                )
//        ))
//    }
//
//    @Test
//    fun end2end_whenCancelledAfterOrderIdIsSet_callsCancelAndFinishesWithCancelled() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//
//        coEvery { mockAuthenticationV1Service.postAuthenticate(any()) }.coAnswers {
//            delay(100)
//            mockk<AuthenticationResponse>().apply {
//                every { orderId }.returns("dummyOrderId")
//                every { isSuccess }.returns(false)
//                every { responseCode }.returns("dummy auth code")
//                every { responseMessage }.returns("dummy auth message")
//                every { detailedResponseCode }.returns("dummy detailed auth code")
//                every { detailedResponseMessage }.returns("dummy detailed auth message")
//                every { language }.returns("dummy language code")
//            }
//        }
//        coEvery { mockCancellationService.postCancel(any()) }.coAnswers {
//            delay(100)
//            mockk {
//                every { responseCode }.returns("dummyResponseCode")
//                every { responseMessage }.returns("dummyResponseMessage")
//                every { detailedResponseCode }.returns("dummyDetailedResponseCode")
//                every { detailedResponseMessage }.returns("dummyDetailedResponseMessage")
//                every { orderId }.returns("orderIdFromServer")
//                every { language }.returns("dummy language code")
//                every { isSuccess }.returns(true)
//            }
//        }
//
//        // When
//        val mockPaymentMethod = createMockPaymentMethod()
//        val mockPaymentData = createMockPaymentData()
//        val viewModel = createPaymentViewModel(mockPaymentData)
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = null,
//                billingAddress = null,
//                shippingAddress = null,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        viewModel.cancelAndFinish()
//        // Mocked cancel call now
//        advanceUntilIdle()
//
//        // Then
//        assertNull(viewModel.orderId)   // Cancellation clears the orderId
//        coVerify { mockCancellationService.postCancel(
//                eq(CancelRequest {
//                    orderId = "dummyOrderId"
//                    reason = "CancelledByUser"
//                }))
//        }
//        stateObserver.assertValue(CardPaymentViewModel.State.Finished(
//                GeideaResult.Cancelled(
//                        responseCode = "dummy auth code",
//                        responseMessage = "dummy auth message",
//                        detailedResponseCode = "dummy detailed auth code",
//                        detailedResponseMessage = "dummy detailed auth message",
//                        orderId = "orderIdFromServer",
//                        language = "dummy language code"
//                )
//        ))
//    }
//
//    @Test
//    fun end2end_when3DSv2andMerchantProvidedPaymentData_happyPath() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockMerchantConfig.useMpgsApiV60 }.returns(true)
//        every { mockOrderResponse.isSuccess }.returns(true)
//        every { mockInitAuthResponse.isSuccess }.returns(true)
//        every { mockInitAuthResponse.redirectHtml }.returns("dummyRedirectHtml")
//        every { mockInitAuthResponse.orderId }.returns("dummyOrderId")
//        every { mockInitAuthResponse.threeDSecureId }.returns("dummyThreeDSecureId")
//        every { mockAuthResponse.isSuccess }.returns(true)
//        coEvery { mockAuthenticationV3Service.postInitiateAuthentication(any()) }.coAnswers {
//            delay(100)
//            mockInitAuthResponse
//        }
//        coEvery { mockAuthenticationV3Service.postAuthenticatePayer(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//        coEvery { mockPaymentService.postPay(any()) }.coAnswers {
//            delay(100)
//            mockOrderResponse
//        }
//
//        // When
//        val mockPaymentMethod = createMockPaymentMethod()
//        val mockPaymentData = createMockPaymentData(paymentMethod = mockPaymentMethod)
//        val viewModel = createPaymentViewModel(mockPaymentData)
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        viewModel.onReturnUrl(CardPaymentViewModel.RETURN_URL + "?code=000")
//        advanceUntilIdle()
//
//        // Then
//        val expectedHistory = arrayOf(
//                CardPaymentViewModel.State.Processing,      // Before Auth call
//                CardPaymentViewModel.State.Redirected(      // After Auth call
//                        htmlBodyContent = "dummyHtml"
//                ),
//                CardPaymentViewModel.State.Processing,      // Before Pay call
//                CardPaymentViewModel.State.Finished(        // After Pay call
//                        GeideaResult.Success(mockOrderResponse.order!!)
//                )
//        )
//        stateObserver.assertValueHistory(*expectedHistory)
//
//        coVerify {
//            mockPaymentService.postPay(
//                    match {
//                        it.orderId == "dummyOrderId"
//                                && it.amount == BigDecimal("233.45")
//                                && it.currency == "SAR"
//                                && it.threeDSecureId == "dummyThreeDSecureId"
//                                && it.paymentMethod == mockPaymentMethod
//                    }
//            )
//        }
//    }
//
//    @Test
//    fun end2end_when3DSv2andSdkProvidedPaymentData_happyPath() = testCoroutineRule.runBlockingTest {
//        // Given
//        every { GeideaPaymentSdk.merchantKey }.returns("mk")
//        every { mockMerchantConfig.useMpgsApiV60 }.returns(true)
//        every { mockOrderResponse.isSuccess }.returns(true)
//        every { mockInitAuthResponse.isSuccess }.returns(true)
//        every { mockInitAuthResponse.redirectHtml }.returns("dummyRedirectHtml")
//        every { mockInitAuthResponse.orderId }.returns("dummyOrderId")
//        every { mockInitAuthResponse.threeDSecureId }.returns("dummyThreeDSecureId")
//        every { mockAuthResponse.isSuccess }.returns(true)
//        every { mockAuthResponse.orderId }.returns("dummyOrderId")
//        coEvery { mockAuthenticationV3Service.postInitiateAuthentication(any()) }.coAnswers {
//            delay(100)
//            mockInitAuthResponse
//        }
//        coEvery { mockAuthenticationV3Service.postAuthenticatePayer(any()) }.coAnswers {
//            delay(100)
//            mockAuthResponse
//        }
//        coEvery { mockPaymentService.postPay(any()) }.coAnswers {
//            delay(100)
//            mockOrderResponse
//        }
//
//        // When
//        val viewModel = createPaymentViewModel(createMockPaymentData())
//        // Mocked load MerchantConfig call now
//
//        val stateObserver = viewModel.stateLiveData.test()
//
//        advanceUntilIdle()
//
//        viewModel.onCardNumberEntered("4111111111111111")
//
//        val mockPaymentMethod = createMockPaymentMethod()
//        viewModel.startFlow(
//                payment = mockPaymentMethod,
//                isMerchantProvidedPayment = false,
//                customerEmail = "mock@email.com",
//                billingAddress = mockAddress,
//                shippingAddress = mockAddress,
//        )
//        // Mocked authenticate call now
//        advanceUntilIdle()
//
//        viewModel.onReturnUrl(CardPaymentViewModel.RETURN_URL + "?code=000")
//        // Mocked pay call now
//        advanceUntilIdle()
//
//        // Then
//        val expectedHistory = arrayOf(
//                CardPaymentViewModel.State.PaymentForm,
//                CardPaymentViewModel.State.Processing,      // Before Init Auth call
//                CardPaymentViewModel.State.Redirected(      // After Init Auth call
//                        htmlBodyContent = "dummyHtml"
//                ),
//                CardPaymentViewModel.State.Processing,      // Before Pay call
//                CardPaymentViewModel.State.Finished(        // After Pay call
//                        GeideaResult.Success(mockOrderResponse.order!!)
//                )
//        )
//        stateObserver.assertValueHistory(*expectedHistory)
//
//        coVerify {
//            mockPaymentService.postPay(
//                    match {
//                        it.orderId == "dummyOrderId"
//                                && it.amount == BigDecimal("233.45")
//                                && it.currency == "SAR"
//                                && it.threeDSecureId == "dummyThreeDSecureId"
//                                && it.paymentMethod == mockPaymentMethod
//                    }
//            )
//        }
//    }
//
//    private fun createPaymentViewModel(paymentData: PaymentData): CardPaymentViewModel {
//        return CardPaymentViewModel(
//                mockAuthenticationV1Service,
//                mockAuthenticationV3Service,
//                mockPaymentService,
//                mockCancellationService,
//                userAgent = "dummyUserAgent",
//                paymentData = paymentData,
//                timeoutMillis = 3_600,
//                merchantConfiguration = mockMerchantConfig,
//                cancelOrderOnDismiss = true,
//        )
//    }
//
//    private fun createMockPaymentData(
//            amount: BigDecimal = "233.45".toBigDecimal(),
//            currency: String = "SAR",
//            orderId: String? = null,
//            callbackURL: String? = "dummyCallbackUrl",
//            paymentOperation: String? = PaymentOperation.PAY,
//            merchantReferenceId: String? = "dummyMerchantReferenceId",
//            customerEmail: String? = "dummyCustomerEmail",
//            billingAddress: Address? = null,
//            shippingAddress: Address? = null,
//            paymentMethod: PaymentMethod? = null,
//            paymentMethods: Set<String>? = null,
//            cardOnFile: Boolean = false,
//            initiatedBy: String? = null,
//            paymentIntentId: String? = null,
//            agreementId: String? = null,
//            agreementType: String? = null,
//            title: String? = null,
//    ): PaymentData {
//        return mockk {
//            val pd = this
//            every { pd.amount }.returns(amount)
//            every { pd.currency }.returns(currency)
//            every { pd.orderId }.returns(orderId)
//            every { pd.paymentOperation }.returns(paymentOperation)
//            every { pd.merchantReferenceId }.returns(merchantReferenceId)
//            every { pd.callbackUrl }.returns(callbackURL)
//            every { pd.customerEmail }.returns(customerEmail)
//            every { pd.billingAddress }.returns(billingAddress)
//            every { pd.shippingAddress }.returns(shippingAddress)
//            every { pd.paymentMethod }.returns(paymentMethod)
//            every { pd.paymentMethods }.returns(paymentMethods)
//            every { pd.cardOnFile }.returns(cardOnFile)
//            every { pd.paymentIntentId }.returns(paymentIntentId)
//            every { pd.initiatedBy }.returns(initiatedBy)
//            every { pd.agreementId }.returns(agreementId)
//            every { pd.agreementType }.returns(agreementType)
//            every { pd.bundle }.returns(mockk {
//                every { getCharSequence(eq(GeideaContract.PARAM_TITLE)) }.returns(title)
//            })
//        }
//    }
//
//    private fun createMockPaymentMethod(): PaymentMethod {
//        return mockk {
//            every { cardNumber }.returns("dummyCardNumber")
//            every { cardHolderName }.returns("dummyCardHolderName")
//            every { cvv }.returns("dummyCVV")
//            every { expiryDate }.returns(
//                    mockk {
//                        every { month }.returns(2)
//                        every { year }.returns(55)
//                    }
//            )
//        }
//    }
//
//    companion object {
//        val supportedCountry1 = Country {
//            key2 = "SA";
//            key3 = "SAU";
//            numericCode = 123
//            nameEn = "Saudi Arabia"
//            nameAr = ""
//            isSupported = true
//        }
//
//        val supportedCountry2 = Country {
//            key2 = "US";
//            key3 = "USA";
//            numericCode = 124
//            nameEn = "USA"
//            nameAr = ""
//            isSupported = true
//        }
//
//        val unsupportedCountry = Country {
//            key2 = "IT";
//            key3 = "ITA";
//            numericCode = 125
//            nameEn = "Italy"
//            nameAr = ""
//            isSupported = false
//        }
//    }
//}