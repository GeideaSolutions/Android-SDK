package net.geidea.paymentsdk.internal.ui.fragment.options

class PaymentOptionsViewModelTest {

    /*@Test
    fun load_withoutCachedConfig_emitsLoadingAndLoaded() = testCoroutineRule.runBlockingTest {
        // Given

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.Loaded(items = listOf(
                        PaymentMethodDescriptor.Card(acceptedBrands = setOf(CardBrand.Visa, CardBrand.Mastercard, CardBrand.Meeza)),
                        PaymentMethodDescriptor.MeezaQr)
                )
        )
    }

    @Test
    fun load_withCachedConfig_emitsLoadingAndLoaded() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantsService.cachedMerchantConfiguration } returns mockMerchantConfig

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.Loaded(items = listOf(
                        PaymentMethodDescriptor.Card(acceptedBrands = setOf(CardBrand.Visa, CardBrand.Mastercard, CardBrand.Meeza)),
                        PaymentMethodDescriptor.MeezaQr)
                )
        )
    }

    @Test
    fun load_whenGetConfigFails_emitsLoadingAndLoadingFailed() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantConfig.isSuccess } returns false
        every { mockMerchantConfig.responseCode } returns "333"
        every { mockMerchantConfig.responseMessage } returns "dummy"
        every { mockMerchantConfig.detailedResponseCode } returns "333.444"
        every { mockMerchantConfig.detailedResponseMessage } returns "dummy"

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.LoadingFailed(GeideaResult.NetworkError(
                        responseCode = "333",
                        responseMessage = "dummy",
                        detailedResponseCode = "333.444",
                        detailedResponseMessage = "dummy",
                        language = "dummyLanguageCode"
                ))
        )
    }

    @Test
    fun load_withDisabledCardBrand_emitsLoadingFailed() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockPaymentData.paymentMethods } returns setOf("amex")  // Amex is disabled in config by default

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.LoadingFailed(GeideaResult.InvalidInputError("Invalid payment method 'amex'"))
        )
    }

    @Test
    fun load_withDisabledMeezaQr_emitsLoadingFailed() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns false
        every { mockPaymentData.paymentMethods } returns setOf("qrcode")

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.LoadingFailed(GeideaResult.InvalidInputError("Invalid payment method 'qrcode'"))
        )
    }

    @Test
    fun load_withMeezaQrOnly_emitsProceedWithMeezaQr() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns true
        every { mockMerchantConfig.paymentMethods } returns setOf("qrcode")

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.ProceedWith(paymentMethod = PaymentMethodDescriptor.MeezaQr)
        )
    }

    @Test
    fun load_withCardsOnly_emitsProceedWithCard() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns false
        every { mockMerchantConfig.paymentMethods } returns setOf("visa", "meeza")

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.ProceedWith(paymentMethod = PaymentMethodDescriptor.Card(
                        acceptedBrands = setOf(CardBrand.Visa, CardBrand.Meeza)
                ))
        )
    }

    @Test
    fun load_withMerchantProvidedCardData_ignoresQrCodeAndEmitsProceedWithCard() = testCoroutineRule.runBlockingTest {
        // Given
        every { mockMerchantConfig.isMeezaQrEnabled } returns true
        every { mockMerchantConfig.paymentMethods } returns setOf("visa", "qrcode")
        every { mockPaymentData.paymentMethod } returns mockk()

        // When
        val viewModel = createViewModel(mockPaymentData)
        val stateObserver = viewModel.stateLiveData.test()
        viewModel.load()
        advanceUntilIdle()

        // Then
        stateObserver.assertValueHistory(
                State.Loading,
                State.ProceedWith(paymentMethod = PaymentMethodDescriptor.Card(
                        acceptedBrands = setOf(CardBrand.Visa)
                ))
        )
    }*/
}