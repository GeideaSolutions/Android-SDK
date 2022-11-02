package net.geidea.paymentsdk.sampleapp

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.api.gateway.GatewayCallbackApi
import net.geidea.paymentsdk.api.paymentintent.PaymentIntentApi
import net.geidea.paymentsdk.flow.GeideaContract
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.OrderItem
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentOptions
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.model.common.AgreementType
import net.geidea.paymentsdk.model.common.InitiatingSource
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.order.CaptureRequest
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.order.RefundRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse
import net.geidea.paymentsdk.model.paymentintent.EInvoiceOrdersResponse
import net.geidea.paymentsdk.model.transaction.PaymentOperation
import net.geidea.paymentsdk.sampleapp.databinding.ActivityMainBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemOrderItemBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemPaymentOptionBinding
import net.geidea.paymentsdk.sampleapp.sample.*
import net.geidea.paymentsdk.sampleapp.sample.OrderItemDialog.collectOrderItems
import net.geidea.paymentsdk.sampleapp.sample.OrderItemDialog.init
import net.geidea.paymentsdk.sampleapp.sample.OrderItemDialog.inputOrderItem
import net.geidea.paymentsdk.sampleapp.sample.PaymentIntentDialogs.inputCreateEInvoiceRequest
import net.geidea.paymentsdk.sampleapp.sample.PaymentOptionItemDialog.choosePaymentOption
import net.geidea.paymentsdk.sampleapp.sample.PaymentOptionItemDialog.collectOptions
import net.geidea.paymentsdk.sampleapp.sample.PaymentOptionItemDialog.init
import net.geidea.paymentsdk.sampleapp.sample.orders.SampleOrdersActivity
import net.geidea.paymentsdk.sampleapp.sample.paymentintents.SamplePaymentIntentsActivity
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentData>

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)
            setSupportActionBar(binding.includeAppBar.toolbar)

            val internalTestMode: Boolean = true    // TODO make configurable
            environmentLabel.isVisible = internalTestMode
            environmentToggleGroup.isVisible = internalTestMode
            if (internalTestMode) {
                // For internal testing

                // Set the last-used server environment
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val apiBaseUrl: String? = sharedPreferences.getString(PREF_KEY_SERVER_ENVIRONMENT, null)
                when (apiBaseUrl) {
                    ServerEnvironment.Dev.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Dev
                        callbackUrlEditText.setText("https://api-dev.gd-azure-dev.net/external-services/api/v1/callback/test123")
                    }
                    ServerEnvironment.Test.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Test
                        callbackUrlEditText.setText("https://api-test.gd-azure-dev.net/external-services/api/v1/callback/test123")
                    }
                    ServerEnvironment.PreProd.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.PreProd
                        callbackUrlEditText.setText("")
                    }
                    ServerEnvironment.Prod.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Prod
                        callbackUrlEditText.setText("")
                    }
                }

                environmentToggleGroup.check(when (GeideaPaymentSdk.serverEnvironment) {
                    ServerEnvironment.Dev -> R.id.devEnvButton
                    ServerEnvironment.Test -> R.id.testEnvButton
                    ServerEnvironment.PreProd -> R.id.preprodEnvButton
                    ServerEnvironment.Prod -> R.id.prodEnvButton
                })
                environmentToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        when (checkedId) {
                            R.id.devEnvButton -> GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Dev
                            R.id.testEnvButton -> GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Test
                            R.id.preprodEnvButton -> GeideaPaymentSdk.serverEnvironment = ServerEnvironment.PreProd
                            R.id.prodEnvButton -> GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Prod
                        }
                        sharedPreferences.edit()
                                .putString(PREF_KEY_SERVER_ENVIRONMENT, GeideaPaymentSdk.serverEnvironment.apiBaseUrl)
                                .apply()
                        clearMerchantLocalStateWithWarning()
                        updateMerchantCredentialsLayout()
                    }
                }
            }

            updateMerchantCredentialsLayout()

            storeCredentialsButton.setOnClickListener { loginMerchant() }
            clearCredentialsButton.setOnClickListener { logoutMerchant() }

            merchantTextView.setOnClickListener {
                lifecycleScope.launch {
                    if (GeideaPaymentSdk.hasCredentials) {
                        onSuccessOf(GatewayApi.getMerchantConfiguration()) { merchantConfig ->
                            this@MainActivity.merchantConfig = merchantConfig
                            AlertDialog.Builder(this@MainActivity)
                                    .setTitle("Merchant configuration")
                                    .setView(monoSpaceTextContainer(merchantConfig.toJson(pretty = true)))
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                        }
                    }
                }
            }

            // Theme drop-down

            val themesAdapter = DropDownAdapter(this@MainActivity, THEMES)
            themeAutoCompleteTextView.setAdapter(themesAdapter)
            if (savedInstanceState == null) {
                themeAutoCompleteTextView.setText(THEMES[0].text, false)
            }

            val paymentOperationsAdapter = DropDownAdapter<String>(this@MainActivity, PAYMENT_OPERATION_ITEMS)
            payOpAutocompleteTextView.setAdapter(paymentOperationsAdapter)
            if (savedInstanceState == null) {
                payOpAutocompleteTextView.setText(PAYMENT_OPERATION_ITEMS[0].text, false)
            }

            paymentOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                TransitionManager.beginDelayedTransition(container)
                if (checkedId != -1) {
                    updateViews(transition = true)
                }
                when (checkedId) {
                    R.id.withCardRadioButton -> {
                        updateViews(transition = true)
                        snack("Payment with plain card data.")
                    }
                    R.id.withTokenRadioButton -> {
                        snack("Merchant provides tokenId of a previously tokenized card.")
                    }
                }
            }

            cardCollectedByButtonToggleGroup.check(R.id.bySdkButton)
            cardCollectedByButtonToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    updateViews(transition = true)
                    when (checkedId) {
                        R.id.bySdkButton -> {
                            snack("SDK will be responsible to collect the customer's card data.")
                        }
                        R.id.byMerchantButton -> {
                            snack("Merchant is responsible to collect customer's card data.")
                        }
                    }
                }
            }

            saveCardCheckbox.setOnCheckedChangeListener { _, isChecked ->
                updateViews(transition = true)
                if (isChecked) {
                    snack("Card tokenization enabled.")
                } else {
                    snack("Card tokenization disabled.")
                }
            }

            // Tokenized card

            lifecycleScope.launch {
                CardRepository.selectedCard.collect { cardItem ->
                    if (cardItem != null) {
                        onCardSelected(cardItem)
                    } else {
                        onCardSelected(null)
                    }
                }
            }
            selectCardButton.setOnClickListener {
                showCardSelectionDialog()
            }

            // SDK Language

            languageToggleGroup.check(when (GeideaPaymentSdk.language) {
                null,
                SdkLanguage.ENGLISH -> R.id.enLanguageButton
                SdkLanguage.ARABIC -> R.id.arLanguageButton
            })
            languageToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.enLanguageButton -> GeideaPaymentSdk.language = SdkLanguage.ENGLISH
                        R.id.arLanguageButton -> GeideaPaymentSdk.language = SdkLanguage.ARABIC
                    }
                }
            }

            // Initiated by drop-down

            val initiatedByAdapter = DropDownAdapter(this@MainActivity, INITIATED_BY_ITEMS)
            initiatedByAutocompleteTextView.setAdapter(initiatedByAdapter)
            initiatedByAutocompleteTextView.addTextChangedListener(afterTextChanged = {
                TransitionManager.beginDelayedTransition(container)
                updateViews(transition = true)
            })
            if (savedInstanceState == null) {
                initiatedByAutocompleteTextView.setText(INITIATED_BY_ITEMS[0].text, false)
            }

            // paymentIntentId

            paymentIntentIdTextInputLayout.setEndIconOnClickListener {
                lifecycleScope.launch {
                    val result = inputCreateEInvoiceRequest()?.let {
                        PaymentIntentApi.createEInvoice(it)
                    }
                    when (result) {
                        is GeideaResult.Success<EInvoiceOrdersResponse> -> {
                            paymentIntentIdEditText.setText(result.data.paymentIntent?.paymentIntentId)
                            snack("e-Invoice created successfully.")
                        }
                        is GeideaResult.Error -> showErrorResult(result.toJson(pretty = true))
                        else -> {}  // Nothing to do
                    }
                }
            }

            // Billing address

            with(expandBillingAddressImageButton) {
                setImageResource(R.drawable.ic_arrow_down)
                billingAddressLinearLayout.isVisible = false

                setOnClickListener {
                    // Animated toggle expand/collapse
                    TransitionManager.beginDelayedTransition(root)
                    if (billingAddressLinearLayout.isVisible) {
                        billingAddressLinearLayout.isVisible = false
                        setImageResource(R.drawable.ic_arrow_down)
                    } else {
                        billingAddressLinearLayout.isVisible = true
                        setImageResource(R.drawable.ic_arrow_up)
                    }
                }
            }

            // Shipping address

            with(expandShippingAddressImageButton) {
                setImageResource(R.drawable.ic_arrow_down)
                shippingAddressLinearLayout.isVisible = false

                setOnClickListener {
                    // Animated toggle expand/collapse
                    TransitionManager.beginDelayedTransition(root)
                    if (shippingAddressLinearLayout.isVisible) {
                        shippingAddressLinearLayout.isVisible = false
                        setImageResource(R.drawable.ic_arrow_down)
                    } else {
                        shippingAddressLinearLayout.isVisible = true
                        setImageResource(R.drawable.ic_arrow_up)
                    }
                }
            }

            // Payment options

            addOptionButton.setOnClickListener {
                lifecycleScope.launch {
                    merchantConfig?.let {
                        val newOption = choosePaymentOption(merchantConfig!!)
                        if (newOption != null) {
                            val itemBinding = ItemPaymentOptionBinding
                                .inflate(layoutInflater)
                                .init(newOption)

                            TransitionManager.beginDelayedTransition(root)
                            paymentOptionItemsLinearLayout.addView(itemBinding.root)
                        }
                    }
                }
            }

            // Order items (required for Shahry and Souhoola Installments)

            addOrderItemButton.setOnClickListener {
                lifecycleScope.launch {
                    val newItem = inputOrderItem()
                    if (newItem != null) {
                        val itemBinding = ItemOrderItemBinding
                                .inflate(layoutInflater)
                                .init(this@MainActivity, newItem)

                        TransitionManager.beginDelayedTransition(root)
                        orderItemsLinearLayout.addView(itemBinding.root)
                    }
                }
            }

            // Agreement type drop-down

            val agreementTypeAdapter = DropDownAdapter(this@MainActivity, AGREEMENT_TYPE_ITEMS)
            agreementTypeAutocompleteTextView.setAdapter(agreementTypeAdapter)
            if (savedInstanceState == null) {
                agreementTypeAutocompleteTextView.setText(AGREEMENT_TYPE_ITEMS[0].text, false)
            }

            // Pay button

            payButton.setOnClickListener {
                if (GeideaPaymentSdk.hasCredentials) {
                    startPayment()
                } else {
                    AlertDialog.Builder(this@MainActivity)
                            .setTitle("Error")
                            .setMessage("Merchant credentials required!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                }
            }

            // This does not handle activity configuration changes correctly for the sake of simplicity.
            // Consider using a ViewModel or Presenter.
            paymentLauncher = registerForActivityResult(PaymentContract(), ::onPaymentResult)
        }
    }

    override fun onStart() {
        super.onStart()

        updateViews(transition = false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val activityClass = when (item.itemId) {
            R.id.item_simplest_payment_sample -> SampleSimplestPaymentActivity::class.java
            R.id.item_paymentformview_sample -> SamplePaymentFormActivity::class.java
            R.id.item_cardinputview_sample -> SampleCardActivity::class.java
            R.id.item_card_fields_sample -> SampleCardFieldsActivity::class.java
            R.id.item_addressinputview_sample -> SampleAddressActivity::class.java
            R.id.item_address_fields_sample -> SampleAddressFieldsActivity::class.java
            R.id.item_payment_intents_sample -> SamplePaymentIntentsActivity::class.java
            R.id.item_orders_sample -> SampleOrdersActivity::class.java
            else -> null
        }
        return if (activityClass != null) {
            startActivity(Intent(this, activityClass))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loginMerchant() = with(binding) {
        val merchantKey: String? = merchantKeyEditText.textOrNull?.trim()
        val merchantPassword: String? = merchantPasswordEditText.textOrNull?.trim()
        when {
            merchantKey == null -> {
                snack("Missing merchant key")
            }
            merchantPassword == null -> {
                snack("Missing merchant password")
            }
            else -> {
                GeideaPaymentSdk.setCredentials(
                        merchantKey = merchantKey,
                        merchantPassword = merchantPassword
                )
                snack("Merchant credentials stored securely")
                updateMerchantCredentialsLayout()
            }
        }
    }

    private fun logoutMerchant() = with(binding) {
        GeideaPaymentSdk.clearCredentials()
        snack("Merchant credentials cleared")
        updateMerchantCredentialsLayout()
        clearMerchantLocalStateWithWarning()
    }

    private fun loadMerchantConfig() = with(binding) {
        lifecycleScope.launch {
            onSuccessOf(GatewayApi.getMerchantConfiguration()) { merchantConfig ->
                this@MainActivity.merchantConfig = merchantConfig
                merchantTextView.text = "Merchant: ${merchantConfig.merchantName ?: ""}"
                val tokenizationEnabled = merchantConfig.isTokenizationEnabled ?: false
                saveCardCheckbox.isVisible = withCardRadioButton.isChecked
                showEmailCheckbox.isVisible = withCardRadioButton.isChecked && bySdkButton.isChecked
                showAddressCheckbox.isVisible = withCardRadioButton.isChecked && bySdkButton.isChecked
                saveCardCheckbox.isEnabled = tokenizationEnabled
                withTokenRadioButton.isEnabled = tokenizationEnabled
                val hasCard = CardRepository.selectedCard.firstOrNull() != null
                tokenizedCardView.isVisible = tokenizationEnabled && hasCard
                selectCardButton.isVisible = tokenizationEnabled

                val currencies = merchantConfig.currencies
                if (!currencies.isNullOrEmpty()) {
                    if (merchantConfig.merchantCountryTwoLetterCode.equals("EG", ignoreCase = true)
                        && currencies.contains("EGP")
                    ) {
                        currencyEditText.setText("EGP")
                    } else {
                        currencyEditText.setText(currencies.first())
                    }
                }

                payButton.isVisible = true
            }
        }
    }

    private var merchantConfig: MerchantConfigurationResponse?
        get() = SampleApplication.INSTANCE.merchantConfiguration
        set(newValue) { SampleApplication.INSTANCE.merchantConfiguration = newValue }

    private fun updateMerchantCredentialsLayout() {
        with(binding) {
            if (GeideaPaymentSdk.hasCredentials) {
                merchantKeyEditText.setText("*****")
                merchantKeyEditText.isEnabled = false
                merchantPasswordEditText.setText("*****")
                merchantPasswordEditText.isEnabled = false
                storeCredentialsButton.isVisible = false
                clearCredentialsButton.isVisible = true
                payButton.isVisible = false

                loadMerchantConfig()

            } else {
                merchantTextView.text = "Merchant"
                merchantKeyEditText.setText("")
                merchantKeyEditText.isEnabled = true
                merchantPasswordEditText.setText("")
                merchantPasswordEditText.isEnabled = true
                storeCredentialsButton.isVisible = true
                clearCredentialsButton.isVisible = false
                payButton.isVisible = false

                withTokenRadioButton.isEnabled = true
                tokenizedCardView.isVisible = false
                selectCardButton.isVisible = false
            }
        }
    }

    private fun updateViews(transition: Boolean = false) {
        if (transition) {
            TransitionManager.beginDelayedTransition(binding.container)
        }
        with(binding) {
            val withCard = withCardRadioButton.isChecked
            val withToken = withTokenRadioButton.isChecked
            val bySdk = cardCollectedByButtonToggleGroup.checkedButtonId == R.id.bySdkButton
            val byMerchant = cardCollectedByButtonToggleGroup.checkedButtonId == R.id.byMerchantButton
            val tokenizationEnabled = merchantConfig?.isTokenizationEnabled == true

            agreementTypeTextInputLayout.isVisible = withCard
            cardCollectedByButtonToggleGroup.isVisible = withCard
            labelCollectedBy.isVisible = withCard
            cardDetailsLinearLayout.isVisible = withCard && byMerchant
            saveCardCheckbox.isVisible = binding.withCardRadioButton.isChecked
            showEmailCheckbox.isVisible = binding.withCardRadioButton.isChecked && bySdk
            showAddressCheckbox.isVisible = binding.withCardRadioButton.isChecked && bySdk
            saveCardCheckbox.isEnabled = tokenizationEnabled
            withTokenRadioButton.isEnabled = tokenizationEnabled
            tokenizedCardView.isVisible = withToken && tokenizedCardView.card != null
            selectCardButton.isVisible = withToken
        }
    }

    private fun onPaymentResult(paymentResult: GeideaResult<Order>) {
        when (paymentResult) {
            is GeideaResult.Success<Order> -> {
                val order: Order = paymentResult.data as Order
                // TODO refund command depends on a configuration flag isRefundEnabled
                showOrder(order, ::capture, ::refund, ::cancel)
                val tokenId = order.tokenId
                if (tokenId != null) {
                    lifecycleScope.launch {
                        CardRepository.saveCard(CardItem(tokenId, order.paymentMethod!!))
                        snack("Tokenized card saved.")
                    }
                }
            }
            is GeideaResult.Error -> {
                showErrorResult(paymentResult.toJson(pretty = true))
            }
            is GeideaResult.Cancelled -> {
                // The payment flow was intentionally cancelled by the user (or timed out)
                showCancellationMessage(paymentResult)
            }
        }
    }

    private suspend fun <T : Parcelable> onSuccessOf(result: GeideaResult<T>, block: suspend (T) -> Unit) {
        when (result) {
            is GeideaResult.Success<T> -> {
                block(result.data)
            }
            is GeideaResult.Error -> {
                showErrorResult(result.toJson(pretty = true))
            }
            is GeideaResult.Cancelled -> {
                showCancellationMessage(result)
            }
        }
    }

    private fun capture(order: Order) {
        GlobalScope.launch(Dispatchers.Main) {
            val request = CaptureRequest {
                orderId = order.orderId
                callbackUrl = order.callbackUrl
            }
            onPaymentResult(GatewayApi.captureOrder(request))
        }
    }

    private fun captureWithCallback(order: Order) {
        val request = CaptureRequest {
            orderId = order.orderId
            callbackUrl = order.callbackUrl
        }
        GatewayCallbackApi.captureOrder(request) { orderResult ->
            check(Thread.currentThread().name == "main")
            onPaymentResult(orderResult)
        }
    }

    private fun refund(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = RefundRequest {
                orderId = order.orderId
                callbackUrl = order.callbackUrl
            }
            onPaymentResult(GatewayApi.refundOrder(request))
        }
    }

    private fun cancel(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = CancelRequest {
                orderId = order.orderId
                reason = "CancelledByUser"
            }
            when (val result = GatewayApi.cancelOrder(request)) {
                is GeideaResult.Success<PaymentResponse> -> showObjectAsJson(result.data)
                is GeideaResult.Error -> showObjectAsJson(result)
                is GeideaResult.Cancelled -> showObjectAsJson(result)
            }
        }
    }

    private fun snack(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.container, message, duration).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("card", binding.tokenizedCardView.card)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.tokenizedCardView.card = savedInstanceState.getParcelable("card")

        updateViews()
    }

    /*
     * Constructs your customer's payment data object used as an input to the payment flow.
     */
    private fun createPaymentData(): PaymentData = with(binding) {
        PaymentData {
            paymentOperation = PAYMENT_OPERATION_ITEMS.findValueByText(payOpAutocompleteTextView.textOrNull)
            amount = amountEditText.textOrNull?.let(::BigDecimal)
            currency = currencyEditText.textOrNull
            merchantReferenceId = merchantRefIdEditText.textOrNull
            callbackUrl = callbackUrlEditText.textOrNull
            customerEmail = customerEmailEditText.textOrNull
            showCustomerEmail = showEmailCheckbox.isChecked
            billingAddress = Address(
                    countryCode = billingCountryCodeEditText.textOrNull,
                    street = billingStreetEditText.textOrNull,
                    city = billingCityEditText.textOrNull,
                    postCode = billingPostCodeEditText.textOrNull
            )
            shippingAddress = Address(
                    countryCode = shippingCountryCodeEditText.textOrNull,
                    street = shippingStreetEditText.textOrNull,
                    city = shippingCityEditText.textOrNull,
                    postCode = shippingPostCodeEditText.textOrNull
            )
            showAddress = showAddressCheckbox.isChecked
            showReceipt = showReceiptCheckbox.isChecked

            when (paymentOptionsRadioGroup.checkedRadioButtonId) {
                R.id.withCardRadioButton -> {
                    if (cardCollectedByButtonToggleGroup.checkedButtonId == R.id.byMerchantButton) {
                        // Merchant handles the card details input, not Geidea SDK. The SDK will skip
                        // the payment form screen and directly start the payment flow.
                        paymentMethod = PaymentMethod {
                            cardHolderName = cardHolderEditText.textOrNull
                            cardNumber = cardNumberEditText.textOrNull

                            val expMonth: Int? = expiryMonthEditText.textOrNull?.toInt()
                            val expYear: Int? = expiryYearEditText.textOrNull?.toInt()
                            expiryDate = if (expMonth != null && expYear != null) {
                                ExpiryDate(
                                    month = expMonth,
                                    year = expYear
                                )
                            } else {
                                null    // SDK will throw IllegalArgumentException
                            }

                            cvv = cvvEditText.textOrNull
                        }
                    } else {
                        // The card details will be collected in SDK payment form
                    }
                }
                R.id.withTokenRadioButton -> {
                    val currentCard = requireNotNull(tokenizedCardView.card) { "Please select a tokenized card!" }
                    tokenId = currentCard.tokenId
                }
            }

            cardOnFile = saveCardCheckbox.isVisible && saveCardCheckbox.isEnabled && saveCardCheckbox.isChecked

            initiatedBy = INITIATED_BY_ITEMS.findValueByText(initiatedByAutocompleteTextView.textOrNull)
            paymentIntentId = paymentIntentIdEditText.textOrNull
            agreementId = agreementIdEditText.textOrNull
            agreementType = AGREEMENT_TYPE_ITEMS.findValueByText(agreementTypeAutocompleteTextView.textOrNull)

            val optionItems = collectOptions(paymentOptionItemsLinearLayout)
            if (optionItems.isNotEmpty()) {
                paymentOptions = PaymentOptions.Builder()
                    .apply {
                        optionItems.forEach { optionItem ->
                            option(
                                label = optionItem.label,
                                paymentMethod = optionItem.paymentMethod
                            )
                        }
                    }
                    .build()
            }

            orderItems = collectOrderItems(orderItemsLinearLayout).takeIf(List<OrderItem>::isNotEmpty)

            bundle = bundleOf(
                GeideaContract.PARAM_THEME to THEMES.findValueByText(themeAutoCompleteTextView.textOrNull),
            )
        }
    }

    private fun startPayment() {
        try {
            val paymentData = createPaymentData()
            paymentLauncher.launch(paymentData)
        } catch (e: IllegalArgumentException) {
            // Missing or invalid argument
            showErrorMessage(e.message)
        } catch (e: NumberFormatException) {
            showErrorMessage(e.message)
        }
    }

    private fun showCancellationMessage(
            cancelResult: GeideaResult.Cancelled,
            onPositive: DialogInterface.OnClickListener? = null
    ) {
        val json = cancelResult.toJson(pretty = true)
        AlertDialog.Builder(this)
                .setTitle("Cancelled")
                .setView(monoSpaceTextContainer(json))
                .setPositiveButton(android.R.string.ok, onPositive)
                .show()
    }

    private fun showCardSelectionDialog() {
        lifecycleScope.launch {
            val tokenCardMap: Map<String, CardItem> = CardRepository.tokenizedCards.first()
            if (tokenCardMap.isNotEmpty()) {
                val dialog = CardListBottomSheetDialog(
                        this@MainActivity,
                        tokenCardMap.values.toList(),
                        ::onClearButtonClicked,
                        ::onCardSelected
                )
                dialog.show()
            } else {
                snack("No tokenized cards.")
            }
        }
    }

    private fun onCardSelected(cardItem: CardItem?) {
        CardRepository.selectCard(cardItem)
        binding.tokenizedCardView.card = cardItem
    }

    private fun onClearButtonClicked() {
        AlertDialog.Builder(this)
                .setMessage("Do you wish to delete all tokenized cards saved on device?")
                .setPositiveButton(R.string.gd_yes) { _, _ -> clearTokenizedCards() }
                .setNeutralButton(android.R.string.cancel, null)
                .show()
    }

    private fun clearTokenizedCards() {
        lifecycleScope.launch {
            try {
                CardRepository.clear()
                snack("All tokenized cards deleted locally.")
            } catch (t: Throwable) {
                snack("Failed to clear tokenized cards!")
            }
        }
    }

    private fun clearMerchantLocalStateWithWarning() {
        lifecycleScope.launch {
            if (!CardRepository.isEmpty()) {
                AlertDialog.Builder(this@MainActivity)
                        .setMessage("All merchant local data (e.g. tokenized cards) will be deleted.")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            this@MainActivity.clearTokenizedCards()
                        }
                        .show()
            }
        }
    }

    companion object {
        val PAYMENT_OPERATION_ITEMS = listOf<DropDownItem<String>>(
                DropDownItem(null, "Default (merchant configuration)"),
                DropDownItem(PaymentOperation.PAY),
                DropDownItem(PaymentOperation.PREAUTHORIZE),
                DropDownItem(PaymentOperation.AUTHORIZE_CAPTURE),
        )

        val INITIATED_BY_ITEMS = listOf<DropDownItem<String>>(
                DropDownItem(InitiatingSource.INTERNET),
                DropDownItem(InitiatingSource.MERCHANT),
        )

        val AGREEMENT_TYPE_ITEMS = listOf<DropDownItem<String>>(
                DropDownItem(null, "None"),
                DropDownItem(AgreementType.RECURRING),
                DropDownItem(AgreementType.INSTALLMENT),
                DropDownItem(AgreementType.UNSCHEDULED),
        )

        const val PREF_KEY_SERVER_ENVIRONMENT = "serverEnvironment"

        val THEMES = listOf(
            // Themes with Geidea branding and styles
            DropDownItem(R.style.Gd_Theme_DayNight_NoActionBar, "Gd.Theme.DayNight.NoActionBar (default)"),
            DropDownItem(R.style.Gd_Theme_Material3_DayNight_NoActionBar, "Gd.Theme.Material3.DayNight.NoActionBar"),

            // Base white-label themes with the default Material colors and styles.
            DropDownItem(R.style.Gd_Base_Theme_DayNight_NoActionBar, "Gd.Base.Theme.DayNight.NoActionBar (white-label)"),
            DropDownItem(R.style.Gd_Base_Theme_NoActionBar, "Gd.Base.Theme.NoActionBar (white-label)"),
            DropDownItem(R.style.Gd_Base_Theme_Light_NoActionBar, "Gd.Base.Theme.Light.NoActionBar (white-label)"),

            // EXPERIMENTAL Material3 themes
            DropDownItem(R.style.Gd_Base_Theme_Material3_DayNight_NoActionBar, "Gd.Base.Theme.Material3.DayNight.NoActionBar (white-label)"),
            DropDownItem(R.style.Gd_Base_Theme_Material3_Light_NoActionBar, "Gd.Base.Theme.Material3.Light.NoActionBar (white-label)"),
            DropDownItem(R.style.Gd_Base_Theme_Material3_DynamicColors_DayNight, "Gd.Base.Theme.Material3.DynamicColors.DayNight (white-label)"),

            // Example themes
            DropDownItem(R.style.Theme_Sdk_Example_Simple, "Theme.Sdk.Example.Simple"),
            DropDownItem(R.style.Theme_Sdk_Example_Outlined, "Theme.Sdk.Example.Outlined"),
            DropDownItem(R.style.Theme_Sdk_Example_Filled, "Theme.Sdk.Example.Filled"),
        )
    }
}