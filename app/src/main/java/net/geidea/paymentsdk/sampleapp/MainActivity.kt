package net.geidea.paymentsdk.sampleapp

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.SdkLanguage
import net.geidea.paymentsdk.model.common.InitiatingSource
import net.geidea.paymentsdk.sampleapp.databinding.ActivityMainBinding
import net.geidea.paymentsdk.sampleapp.sample.SampleAddressActivity
import net.geidea.paymentsdk.sampleapp.sample.SampleAddressFieldsActivity
import net.geidea.paymentsdk.sampleapp.sample.SampleCardActivity
import net.geidea.paymentsdk.sampleapp.sample.SampleCardFieldsActivity
import net.geidea.paymentsdk.sampleapp.sample.SamplePaymentFormActivity
import net.geidea.paymentsdk.sampleapp.sample.SampleSimplestPaymentActivity
import net.geidea.paymentsdk.sampleapp.sample.orders.SampleOrdersActivity
import net.geidea.paymentsdk.sampleapp.sample.paymentintents.SamplePaymentIntentsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(applicationContext)
                when (sharedPreferences.getString(PREF_KEY_SERVER_ENVIRONMENT, null)) {

                    ServerEnvironment.PreProd.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.PreProd
                        callbackUrlEditText.setText("")
                    }

                    ServerEnvironment.Prod.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Prod
                        callbackUrlEditText.setText("")
                    }
                }

                environmentToggleGroup.check(
                    when (GeideaPaymentSdk.serverEnvironment) {
                        ServerEnvironment.Prod -> R.id.prodEnvButton
                        else -> {
                            R.id.preprodEnvButton
                        }
                    }
                )
                environmentToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        when (checkedId) {
                            R.id.preprodEnvButton -> GeideaPaymentSdk.serverEnvironment =
                                ServerEnvironment.PreProd

                            R.id.prodEnvButton -> GeideaPaymentSdk.serverEnvironment =
                                ServerEnvironment.Prod
                        }
                        sharedPreferences.edit()
                            .putString(
                                PREF_KEY_SERVER_ENVIRONMENT,
                                GeideaPaymentSdk.serverEnvironment.apiBaseUrl
                            )
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

            // SDK Language
            languageToggleGroup.check(
                when (GeideaPaymentSdk.language) {
                    null,
                    SdkLanguage.ENGLISH -> R.id.enLanguageButton

                    SdkLanguage.ARABIC -> R.id.arLanguageButton
                }
            )
            languageToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.enLanguageButton -> GeideaPaymentSdk.language = SdkLanguage.ENGLISH
                        R.id.arLanguageButton -> GeideaPaymentSdk.language = SdkLanguage.ARABIC
                    }
                }
            }

            matchShippingAddressCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    shippingCityEditText.text = billingCityEditText.text
                    shippingCountryCodeEditText.text = billingCountryCodeEditText.text
                    shippingStreetEditText.text = billingStreetEditText.text
                    shippingPostCodeEditText.text = billingPostCodeEditText.text
                }
            }
            // Initiated by drop-down
            val initiatedByAdapter = DropDownAdapter(this@MainActivity, INITIATED_BY_ITEMS)
            initiatedByAutocompleteTextView.setAdapter(initiatedByAdapter)
            initiatedByAutocompleteTextView.addTextChangedListener(afterTextChanged = {
                TransitionManager.beginDelayedTransition(container)
            })
            if (savedInstanceState == null) {
                initiatedByAutocompleteTextView.setText(INITIATED_BY_ITEMS[0].text, false)
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

            saveButton.setOnClickListener {
                if (GeideaPaymentSdk.hasCredentials) {
                    storePaymentConfig()
                } else {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Error")
                        .setMessage("Merchant credentials required!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateViews()
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
        //todo - remove hardcoded values
        val merchantKey: String? = merchantKeyEditText.textOrNull?.trim() ?: BuildConfig.API_KEY
        val merchantPassword: String? = merchantPasswordEditText.textOrNull?.trim() ?: BuildConfig.API_PASS
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
                saveButton.isVisible = true
            }
        }
    }

    private var merchantConfig: MerchantConfigurationResponse?
        get() = SampleApplication.INSTANCE.merchantConfiguration
        set(newValue) {
            SampleApplication.INSTANCE.merchantConfiguration = newValue
        }

    private fun updateMerchantCredentialsLayout() {
        with(binding) {
            if (GeideaPaymentSdk.hasCredentials) {
                merchantKeyEditText.setText("*****")
                merchantKeyEditText.isEnabled = false
                merchantPasswordEditText.setText("*****")
                merchantPasswordEditText.isEnabled = false
                storeCredentialsButton.isVisible = false
                clearCredentialsButton.isVisible = true
                saveButton.isVisible = false

                loadMerchantConfig()

            } else {
                merchantTextView.text = "Merchant"
                merchantKeyEditText.setText("")
                merchantKeyEditText.isEnabled = true
                merchantPasswordEditText.setText("")
                merchantPasswordEditText.isEnabled = true
                storeCredentialsButton.isVisible = true
                clearCredentialsButton.isVisible = false
                saveButton.isVisible = false
            }
        }
    }

    private suspend fun <T : Parcelable> onSuccessOf(
        result: GeideaResult<T>,
        block: suspend (T) -> Unit
    ) {
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

    private fun snack(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.container, message, duration).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateViews()
    }

    private fun updateViews() {
        with(binding) {
            currencyEditText.setText(sharedPreferences.getStringOrNull("currency"))
            merchantRefIdEditText.setText(sharedPreferences.getStringOrNull("merchantReferenceId"))
            callbackUrlEditText.setText(sharedPreferences.getStringOrNull("callbackUrl"))

            customerEmailEditText.setText(sharedPreferences.getStringOrNull("customerEmail"))
            billingCountryCodeEditText.setText(sharedPreferences.getStringOrNull("countryCode"))
            billingStreetEditText.setText(sharedPreferences.getStringOrNull("street"))
            billingCityEditText.setText(sharedPreferences.getStringOrNull("city"))
            billingPostCodeEditText.setText(sharedPreferences.getStringOrNull("postCode"))

            shippingCountryCodeEditText.setText(sharedPreferences.getStringOrNull("shippingCountryCode"))
            shippingStreetEditText.setText(sharedPreferences.getStringOrNull("shippingStreet"))
            shippingCityEditText.setText(sharedPreferences.getStringOrNull("shippingCity"))
            shippingPostCodeEditText.setText(sharedPreferences.getStringOrNull("shippingPostCode"))

            //load default values if not configured already
            initiatedByAutocompleteTextView.setText(
                sharedPreferences.getString(
                    "initiatedBy",
                    INITIATED_BY_ITEMS[0].text
                )
            )
            themeAutoCompleteTextView.setText(
                sharedPreferences.getString(
                    "themeTitle",
                    THEMES[0].text
                )
            )

            paymentOptions.showAddressCheckbox.isChecked =
                sharedPreferences.getBoolean("showAddress", false);
            paymentOptions.showReceiptCheckbox.isChecked =
                sharedPreferences.getBoolean("showReceipt", false);
            paymentOptions.showEmailCheckbox.isChecked =
                sharedPreferences.getBoolean("showCustomerEmail", false)
            paymentOptions.showGeideaLogoCheckbox.isChecked =
                sharedPreferences.getBoolean("showGeideaLogo", true);
        }
    }

    /*
     * Constructs your customer's payment data object used as an input to the payment flow.
     */
    private fun storePaymentConfig() = with(binding) {
        val currency = currencyEditText.textOrNull
        val merchantReferenceId = merchantRefIdEditText.textOrNull
        val callbackUrl = callbackUrlEditText.textOrNull
        val customerEmail = customerEmailEditText.textOrNull

        val countryCode = billingCountryCodeEditText.textOrNull
        val street = billingStreetEditText.textOrNull
        val city = billingCityEditText.textOrNull
        val postCode = billingPostCodeEditText.textOrNull


        val shippingCountryCode = shippingCountryCodeEditText.textOrNull
        val shippingStreet = shippingStreetEditText.textOrNull
        val shippingCity = shippingCityEditText.textOrNull
        val shippingPostCode = shippingPostCodeEditText.textOrNull

        val initiatedBy =
            INITIATED_BY_ITEMS.findValueByText(initiatedByAutocompleteTextView.textOrNull)

        val theme = THEMES.findValueByText(themeAutoCompleteTextView.textOrNull) ?: 0
        val themeTitle = themeAutoCompleteTextView.textOrNull

        val showAddress = binding.paymentOptions.showAddressCheckbox.isChecked
        val showReceipt = binding.paymentOptions.showReceiptCheckbox.isChecked
        val showCustomerEmail = binding.paymentOptions.showEmailCheckbox.isChecked
        val showGeideaLogo = binding.paymentOptions.showGeideaLogoCheckbox.isChecked

        sharedPreferences.edit().apply {
            putString("currency", currency)
            putString("merchantReferenceId", merchantReferenceId)
            putString("callbackUrl", callbackUrl)
            putString("customerEmail", customerEmail)
            putString("countryCode", countryCode)
            putString("street", street)
            putString("city", city)
            putString("postCode", postCode)
            putString("shippingCountryCode", shippingCountryCode)
            putString("shippingStreet", shippingStreet)
            putString("shippingCity", shippingCity)
            putString("shippingPostCode", shippingPostCode)
            putString("initiatedBy", initiatedBy)
            putBoolean("showAddress", showAddress)
            putBoolean("showReceipt", showReceipt)
            putBoolean("showCustomerEmail", showCustomerEmail)
            putBoolean("showGeideaLogo", showGeideaLogo)
            putInt("theme", theme)
            putString("themeTitle", themeTitle)

        }.apply()

        hideKeyboard(binding.root)

        AlertDialog.Builder(this@MainActivity)
            .setTitle("Success")
            .setMessage("Configuration updated successfully")
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                finish()
            }
            .show()
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

    private fun onCardSelected(cardItem: CardItem?) {
        CardRepository.selectCard(cardItem)
//        binding.tokenizedCardView.card = cardItem
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

        val INITIATED_BY_ITEMS = listOf(
            DropDownItem(InitiatingSource.INTERNET)
        )

        const val PREF_KEY_SERVER_ENVIRONMENT = "serverEnvironment"

        val THEMES = listOf(
            // Themes with Geidea branding and styles
            DropDownItem(
                R.style.Gd_Theme_DayNight_NoActionBar,
                "Gd.Theme.DayNight.NoActionBar (default)"
            ),
            DropDownItem(
                R.style.Gd_Theme_Material3_DayNight_NoActionBar,
                "Gd.Theme.Material3.DayNight.NoActionBar"
            ),

            // Base white-label themes with the default Material colors and styles.
            DropDownItem(
                R.style.Gd_Base_Theme_DayNight_NoActionBar,
                "Gd.Base.Theme.DayNight.NoActionBar (white-label)"
            ),
            DropDownItem(
                R.style.Gd_Base_Theme_NoActionBar,
                "Gd.Base.Theme.NoActionBar (white-label)"
            ),
            DropDownItem(
                R.style.Gd_Base_Theme_Light_NoActionBar,
                "Gd.Base.Theme.Light.NoActionBar (white-label)"
            ),

            // EXPERIMENTAL Material3 themes
            DropDownItem(
                R.style.Gd_Base_Theme_Material3_DayNight_NoActionBar,
                "Gd.Base.Theme.Material3.DayNight.NoActionBar (white-label)"
            ),
            DropDownItem(
                R.style.Gd_Base_Theme_Material3_Light_NoActionBar,
                "Gd.Base.Theme.Material3.Light.NoActionBar (white-label)"
            ),
            DropDownItem(
                R.style.Gd_Base_Theme_Material3_DynamicColors_DayNight,
                "Gd.Base.Theme.Material3.DynamicColors.DayNight (white-label)"
            ),

            // Example themes
            DropDownItem(R.style.Theme_Sdk_Example_Simple, "Theme.Sdk.Example.Simple"),
            DropDownItem(R.style.Theme_Sdk_Example_Outlined, "Theme.Sdk.Example.Outlined"),
            DropDownItem(R.style.Theme_Sdk_Example_Filled, "Theme.Sdk.Example.Filled"),
        )
    }
}