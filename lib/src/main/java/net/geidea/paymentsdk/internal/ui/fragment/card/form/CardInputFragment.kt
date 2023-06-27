package net.geidea.paymentsdk.internal.ui.fragment.card.form

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import net.geidea.paymentsdk.GdCardGraphArgs
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentCardInputBinding
import net.geidea.paymentsdk.databinding.GdIncludeAlternativePmSelectorBinding
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.ui.fragment.card.cardNavGraphViewModel
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.ui.widget.setup
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import net.geidea.paymentsdk.ui.widget.address.DefaultCountryDropDownAdapter
import net.geidea.paymentsdk.ui.widget.card.CardFieldType
import net.geidea.paymentsdk.ui.widget.card.CardInputAdapter
import net.geidea.paymentsdk.ui.widget.card.CardInputView
import java.math.BigDecimal


internal class CardInputFragment : BaseFragment<CardInputViewModel>(R.layout.gd_fragment_card_input) {

    private val args: CardInputFragmentArgs by navArgs()
    private val cardGraphArgs: GdCardGraphArgs by navArgs()

    private val binding by viewBinding(GdFragmentCardInputBinding::bind)

    private val cardPaymentViewModel: BaseCardPaymentViewModel by cardNavGraphViewModel(lazy { paymentViewModel }, lazy { cardGraphArgs })

    override val viewModel: CardInputViewModel by viewModels { ViewModelFactory(
        cardPaymentViewModel = cardPaymentViewModel,
        step = args.step,
        merchantConfiguration = merchantConfig,
    ) }

    val merchantConfig: MerchantConfigurationResponse get() {
        // Retrieve the merchant config from the global instance, because it is too large to put it in the
        // fragment arguments Bundle due to risk of TransactionTooLargeException.
        return SdkComponent.merchantsService.cachedMerchantConfiguration
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            val paymentData = viewModel.paymentData

            if (paymentData.paymentMethod != null) {
                logi("CardInputFragment: paying with merchant-provided payment data")
            } else {
                logi("CardInputFragment: paying with payment data collected by SDK")
            }

            appBarWithStepper.setup(args.step, underlayView = binding.paymentFormScrollView)
            appBarWithStepper.stepper.isBackButtonVisible = false
            addBackListener { viewModel.onBackPressed() }

            initAlternativeMethodsSelector(alternativeMethodSelector)

            // And yet another Back button
            backButton.isVisible = true
            backButton.setOnClickListener { viewModel.onBackPressed() }

            // Show the outer BNPL payment method above Geidea logo
            paymentViewModel.bnplPaymentMethod?.let {
                bnplLogoImageView.isVisible = true
                it.embeddableLogo?.let(bnplLogoImageView::setImageResource)
            }

            paymentFormView.acceptedCardBrands = viewModel.acceptedCardBrands

            paymentFormView.addCardInputListener(object : CardInputAdapter() {
                override fun onFieldValidStatus(field: CardFieldType) {
                    if (field == CardFieldType.NUMBER) {
                        // Gather all the input data except the card details
                        val paymentData = createFinalPaymentData(paymentData, skipCardDetails = true)
                        viewModel.onCardNumberEntered(paymentFormView.cardNumber, paymentData)
                    }
                }
            })

            // When auth fails and user returns to this screen previous card input should be cleared
            paymentFormView.findViewById<CardInputView>(R.id.cardInputView)
                .forAllDescendants { childView -> childView.isSaveEnabled = false }

            with (initAuthWebView) {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webChromeClient = object : WebChromeClient() {}
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        logi("HTTPS initAuthWebView onPageStarted(url='$url')")
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        logi("HTTPS initAuthWebView onPageFinished(url='$url')")
                    }

                    override fun onLoadResource(view: WebView, url: String) {
                        logi("HTTPS initAuthWebView onLoadResource(url='$url')")
                    }
                }
            }

            viewModel.deviceInfo = initAuthWebView.retrieveDeviceInfo()

            viewModel.htmlLiveData.observe(viewLifecycleOwner) { redirectHtml ->
                initAuthWebView.loadDataWithBaseURL("", redirectHtml, "text/html", "UTF-8", null)
            }

            viewModel.clearCardLiveEvent.observe(viewLifecycleOwner) {
                binding.paymentFormScrollView.requestFocus()
                paymentFormView.clearCard()
            }

            // Country code

            val countries: Set<Country> = merchantConfig.countries ?: emptySet()

            val billingCountryCode: String? = paymentData.billingAddress?.countryCode
            if (!billingCountryCode.isNullOrEmpty() && countries.find {
                        billingCountryCode.equals(it.key3, ignoreCase = true) } == null
            ) {
                error("Invalid billing address")
            }
            val shippingCountryCode: String? = paymentData.shippingAddress?.countryCode
            if (!shippingCountryCode.isNullOrEmpty() && countries.find {
                        shippingCountryCode.equals(it.key3, ignoreCase = true) } == null
            ) {
                error("Invalid shipping address")
            }

            initCountryAdapters(countries)
            if (savedInstanceState == null) {
                // Only on first inflation
                updateCountryViewsAndCheckbox(paymentData)
            }

            paymentFormView.setOnValidityChangedListener { _, valid ->
                payButton.isEnabled = valid
            }
            paymentFormView.card

            val onTextFieldChange = object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    viewModel.onCardFieldChanged()
                }
            }

            with(paymentFormView.cardInputView) {
                addCardHolderTextWatcher(onTextFieldChange)
                addCardNumberTextWatcher(onTextFieldChange)
                addExpiryDateTextWatcher(onTextFieldChange)
                addSecurityCodeTextWatcher(onTextFieldChange)
            }

            viewModel.payButtonEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
                payButton.isEnabled = enabled
            }
            payButton.text = formatPayButtonText(paymentData.amount, paymentData.currency)
            payButton.setOnClickListener {
                runSafely(::onPayButtonClicked)
            }

            cancelButton.setOnClickListener {
                runSafely(::onCancelButtonClicked)
            }
            viewModel.cancelButtonLiveData.observe(viewLifecycleOwner) { enabled ->
                cancelButton.isEnabled = enabled
            }

            paymentViewModel.clientTimeoutLiveEvent.observe(viewLifecycleOwner) {
                // Clear focus to avoid validation message showing up
                requireActivity().currentFocus?.clearFocus()
                paymentFormView.clearCard()
            }

            if (savedInstanceState == null) {
                runSafely {
                    with(paymentFormView) {
                        showCustomerEmail = paymentData.showCustomerEmail
                        customerEmail = paymentData.customerEmail
                        showAddress = paymentData.showAddress
                        paymentData.billingAddress?.let { billingAddress = it }
                        paymentData.shippingAddress?.let { shippingAddress = it }
                    }
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        requireActivity().currentFocus?.clearFocus()
    }

    private fun initAlternativeMethodsSelector(
        alternativeMethodSelector: GdIncludeAlternativePmSelectorBinding,
    ) {
        alternativeMethodSelector.root.isVisible = false

        val firstAlternativeMethod = viewModel.alternativePaymentMethods.firstOrNull()
        with(alternativeMethodSelector.alternativeMethodButton) {
            isVisible = firstAlternativeMethod != null
            if (firstAlternativeMethod != null) {

                val logoResId = firstAlternativeMethod.logo ?: 0
                setIconResource(logoResId)
                if (firstAlternativeMethod is BnplPaymentMethodDescriptor) {
                    setText(R.string.gd_pm_group_bnpl)
                } else {
                    setText(firstAlternativeMethod.text)
                }
                setOnClickListener {
                    viewModel.onAlternativePaymentMethodClicked(firstAlternativeMethod)
                }

                val currentMethodName = resourceText(R.string.gd_pm_alternative_card)
                alternativeMethodSelector.alternativeMethodDividerTextView.setText(
                    templateText(R.string.gd_or_pay_with_s, currentMethodName)
                )
            }
        }
        with(alternativeMethodSelector.showAllMethodsButton) {
            isVisible = viewModel.alternativePaymentMethods.size > 1
            setOnClickListener { viewModel.onShowAllPaymentMethodsClicked() }
        }
        viewModel.alternativeMethodSelectorVisible.observe(viewLifecycleOwner) { visible ->
            // Slight delay + animation to catch user's attention
            requireView().postDelayed({
                TransitionManager.beginDelayedTransition(binding.coordinatorLayout)
                alternativeMethodSelector.constraintLayout.isVisible = visible
                binding.payWithCardLinearLayout.isVisible = !visible
            }, 300)
        }
    }

    private fun initCountryAdapters(countries: Set<Country>) {
        val countryList = countries.toList()

        with(binding.paymentFormView) {
            val billingCountriesAdapter = DefaultCountryDropDownAdapter(requireContext(), countryList)
            setBillingCountryDropDownAdapter(billingCountriesAdapter)

            val shippingCountriesAdapter = DefaultCountryDropDownAdapter(requireContext(), countryList)
            setShippingCountryDropDownAdapter(shippingCountriesAdapter)
        }
    }

    private fun updateCountryViewsAndCheckbox(paymentData: PaymentData) {
        with(binding.paymentFormView) {

            billingAddressCountryCode = viewModel.getPrePopulatedBillingCountryCode(merchantConfig)
            shippingAddressCountryCode = viewModel.getPrePopulatedShippingCountryCode(merchantConfig)

            this.isSameAddressChecked = billingAddress.equalsIgnoreCase(shippingAddress)
                    || paymentData.shippingAddress.isNullOrEmpty()
        }
    }

    private fun clearFocus() {
        // Clears the current focus by focusing the parent
        binding.paymentFormScrollView.requestFocus()
    }

    private fun onPayButtonClicked() {
        logi("CardInputFragment.onPayButtonClicked()")

        val finalPaymentData = createFinalPaymentData(viewModel.paymentData)

        viewModel.onPayButtonClicked(finalPaymentData)

        clearFocus()
        hideKeyboard(requireActivity())
    }

    private fun onCancelButtonClicked() {
        confirmCancellationDialog(onPositive = { _, _ ->
            logi("Cancel confirmed")
            viewModel.onCancelConfirmed()
        })
    }

    /**
     * Create [PaymentData] that is merged from the [initialPaymentData] and the data overridden by
     * the user in the payment form (card data, addresses or email).
     */
    private fun createFinalPaymentData(
        initialPaymentData: PaymentData,
        skipCardDetails: Boolean = false,
    ): PaymentData = with(binding) {
        return PaymentData {
            // Use the initial intent as a default
            paymentOperation = initialPaymentData.paymentOperation
            amount = initialPaymentData.amount
            currency = initialPaymentData.currency
            merchantReferenceId = initialPaymentData.merchantReferenceId
            callbackUrl = initialPaymentData.callbackUrl
            customerEmail = initialPaymentData.customerEmail
            showCustomerEmail = initialPaymentData.showCustomerEmail
            billingAddress = initialPaymentData.billingAddress
            shippingAddress = initialPaymentData.shippingAddress
            showAddress = initialPaymentData.showAddress
            cardOnFile = initialPaymentData.cardOnFile
            initiatedBy = initialPaymentData.initiatedBy
            agreementId = initialPaymentData.agreementId
            agreementType = initialPaymentData.agreementType
            bundle = initialPaymentData.bundle

            if (!skipCardDetails) {
                val card: Card? = paymentFormView.card
                paymentMethod = PaymentMethod {
                    cardHolderName = card?.cardHolderName?.trim()
                    cardNumber = card?.cardNumber
                    expiryDate = card?.expiryDate
                    cvv = card?.cvv
                }
            }

            customerEmail = if (paymentFormView.showCustomerEmail) {
                paymentFormView.customerEmail
            } else {
                initialPaymentData.customerEmail
            }

            billingAddress = if (paymentFormView.showAddress) {
                paymentFormView.billingAddress
            } else {
                initialPaymentData.billingAddress
            }

            shippingAddress = if (paymentFormView.showAddress) {
                if (paymentFormView.isSameAddressChecked) {
                    paymentFormView.billingAddress
                } else {
                    paymentFormView.shippingAddress
                }
            } else {
                initialPaymentData.shippingAddress
            }
        }
    }

    private fun formatPayButtonText(amount: BigDecimal, currency: String): CharSequence {
        val currencyFormat = currencyFormat(currency)
        val amountAndCurrency: String = currencyFormat.format(amount)
        return getString(R.string.gd_button_pay_s, amountAndCurrency)
    }

    private class ViewModelFactory(
        private val cardPaymentViewModel: BaseCardPaymentViewModel,
        private val step: Step?,
        private val merchantConfiguration: MerchantConfigurationResponse,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardInputViewModel(
                cardPaymentViewModel = cardPaymentViewModel,
                downPaymentAmount = cardPaymentViewModel.downPaymentAmount,
                step = step,
                connectivity = SdkComponent.connectivity,
            ) as T
        }
    }
}