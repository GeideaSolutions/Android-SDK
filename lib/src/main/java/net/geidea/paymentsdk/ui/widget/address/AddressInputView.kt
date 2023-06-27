package net.geidea.paymentsdk.ui.widget.address

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.ListAdapter
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.LocaleUtils.localeLanguage
import net.geidea.paymentsdk.internal.util.textOrNull
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.Country
import net.geidea.paymentsdk.ui.validation.FieldMaxLengthValidator
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidCityLength
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidPostCodeLength
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidStreetLength
import net.geidea.paymentsdk.ui.validation.address.validator.DefaultCountryValidator
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener


/**
 * Composite view for entering and validation of either a billing or a shipping [address][Address].
 *
 * Consists of 4 child views for input of the respective [Address] properties where each of the
 * fields is wrapped inside [TextInputLayout]:
 *
 * 1. [CountryAutoCompleteTextView] - displays the current country selected from a drop-down menu
 * with countries. Countries must first be supplied by calling [setCountryDropDownAdapter].
 * 2. [StreetEditText] - edit field for street name and number.
 * 3. [CityEditText] - edit field for city.
 * 4. [PostCodeEditText] - edit field for postal code.
 *
 * @see Address
 */
open class AddressInputView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    private lateinit var countryInputLayout: TextInputLayout
    private lateinit var countryAutoCompleteTextView: CountryAutoCompleteTextView
    private lateinit var cityInputLayout: TextInputLayout
    private lateinit var cityEditText: CityEditText
    private lateinit var postCodeInputLayout: TextInputLayout
    private lateinit var postCodeEditText: PostCodeEditText
    private lateinit var streetInputLayout: TextInputLayout
    private lateinit var streetEditText: StreetEditText

    private var isRestoringViewState = false

    /**
     * Listener for address input events.
     */
    private var addressInputListener: AddressInputListener? = null

    init {
        initView()
    }

    /**
     * Responsible for view inflation and initialization of child views.
     */
    private fun initView() {
        inflate(context, R.layout.gd_view_address, this)

        orientation = VERTICAL

        countryInputLayout = findViewById(R.id.countryInputLayout)
        countryAutoCompleteTextView = findViewById(R.id.countryAutoCompleteTextView)

        streetInputLayout = findViewById(R.id.streetInputLayout)
        streetEditText = findViewById(R.id.streetEditText)

        cityInputLayout = findViewById(R.id.cityInputLayout)
        cityEditText = findViewById(R.id.cityEditText)

        postCodeInputLayout = findViewById(R.id.postCodeInputLayout)
        postCodeEditText = findViewById(R.id.postCodeEditText)

        // Add error listeners and messages

        countryAutoCompleteTextView.setOnErrorListener(TextInputErrorListener(countryInputLayout))
        streetEditText.setOnErrorListener(TextInputErrorListener(streetInputLayout))
        cityEditText.setOnErrorListener(TextInputErrorListener(cityInputLayout))
        postCodeEditText.setOnErrorListener(TextInputErrorListener(postCodeInputLayout))

        // Input fields validation change listeners

        // Country

        val countryValidator = DefaultCountryValidator(
                areCountriesLoadedFun = { countryAutoCompleteTextView.adapter != null },
                countryLookupFun = ::countryLookup
        )
        countryAutoCompleteTextView.setCountryValidator(countryValidator)
        countryAutoCompleteTextView.setOnValidStatusListener {
            validate()
        }
        countryAutoCompleteTextView.setOnInvalidStatusListener { value, status ->
            validate()
            if (!isRestoringViewState && countryAutoCompleteTextView.adapter != null) {
                countryAutoCompleteTextView.updateErrorMessage()
            }
        }

        // City

        cityEditText.setValidator(FieldMaxLengthValidator { InvalidCityLength })
        cityEditText.setOnValidStatusListener {
            validate()
        }
        cityEditText.setOnInvalidStatusListener { value, status ->
            validate()
            if (!value.isNullOrEmpty()) {
                cityEditText.updateErrorMessage()
            }
        }

        // Street

        streetEditText.setValidator(FieldMaxLengthValidator { InvalidStreetLength })
        streetEditText.setOnValidStatusListener {
            validate()
        }
        streetEditText.setOnInvalidStatusListener { value, status ->
            validate()
            if (!value.isNullOrEmpty()) {
                streetEditText.updateErrorMessage()
            }
        }

        // Postcode

        postCodeEditText.setValidator(FieldMaxLengthValidator { InvalidPostCodeLength })
        postCodeEditText.setOnValidStatusListener {
            validate()
        }
        postCodeEditText.setOnInvalidStatusListener { value, status ->
            validate()
            if (!value.isNullOrEmpty()) {
                postCodeEditText.updateErrorMessage()
            }
        }

        initFocusChangeListeners()
    }

    /**
     * Update the validation status of this composite view derived from the validation statuses of
     * all its children input fields.
     */
    private fun validate() {
        isValid = areAllFieldsValid()
    }

    private fun areAllFieldsValid(): Boolean {
        return countryAutoCompleteTextView.isValid &&
            cityEditText.isValid &&
            streetEditText.isValid &&
            postCodeEditText.isValid
    }

    private fun initFocusChangeListeners() {
        countryAutoCompleteTextView.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addressInputListener?.onFocusChange(AddressFieldType.COUNTRY)
            }
        }

        streetEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addressInputListener?.onFocusChange(AddressFieldType.STREET)
            }
        }

        cityEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addressInputListener?.onFocusChange(AddressFieldType.CITY)
            }
        }

        postCodeEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addressInputListener?.onFocusChange(AddressFieldType.POSTCODE)
            }
        }
    }

    /**
     * Returns true if the currently input address data is valid or false otherwise.
     */
    var isValid: Boolean = false
        private set(value) {
            if (field != value) {
                field = value
                addressInputListener?.onAddressValidationChanged(address, value)
            }
        }

    /**
     * The [address][Address] entered in this view. Could be valid or invalid.
     *
     * @see isValid
     */
    var address: Address
        /**
         * Get an [address][Address]. Could be valid or invalid.
         *
         * @see isValid
         */
        get() {
            return Address(
                    countryCode = countryAutoCompleteTextView.country?.key3,
                    street = streetEditText.textOrNull,
                    city = cityEditText.textOrNull,
                    postCode = postCodeEditText.textOrNull,
            )
        }
        /**
         * Set an [address][Address] in this view by setting the corresponding input fields.
         */
        set(value) {
            countryCode = value.countryCode
            street = value.street
            city = value.city
            postCode = value.postCode
        }

    /**
     * Clears the input fields and returns them to normal state (if they are already in error state).
     */
    fun clear() {
        countryAutoCompleteTextView.setText("")
        streetEditText.setText("")
        cityEditText.setText("")
        postCodeEditText.setText("")
    }

    /**
     * Enables or disables this view and all of its children.
     *
     * @param enabled flag indicating if this view should be enabled.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        countryInputLayout.isEnabled = enabled
        streetInputLayout.isEnabled = enabled
        cityInputLayout.isEnabled = enabled
        postCodeInputLayout.isEnabled = enabled

        countryAutoCompleteTextView.isEnabled = enabled
        streetEditText.isEnabled = enabled
        cityEditText.isEnabled = enabled
        postCodeEditText.isEnabled = enabled
    }

    /**
     * Set adapter for the Country autocomplete text view.
     *
     * The adapter should be populated with the countries currently supported by you as a Merchant
     * (obtainable with [net.geidea.paymentsdk.api.gateway.GatewayApi.getMerchantConfiguration]).
     *
     * For an easy to way to create a drop-down menu adapter use:
     * ```
     * val menuAdapter = DefaultCountryDropDownAdapter(merchantConfiguration.supportedCountries)
     * setCountryDropDownAdapter(menuAdapter)
     * ```
     *
     * @see DefaultCountryDropDownAdapter
     */
    fun <T> setCountryDropDownAdapter(adapter: T) where T : ListAdapter, T : Filterable {
        this.countryAutoCompleteTextView.setAdapter(adapter)
    }

    /**
     * Sets a listener to be notified when the country is changed.
     */
    fun setOnCountryChangedListener(listener: OnCountryChangedListener?) {
        countryAutoCompleteTextView.setOnCountryChangedListener(listener)
    }

    /**
     * Sets a listener for address input events.
     */
    fun setAddressInputListener(listener: AddressInputListener) {
        this.addressInputListener = listener
        // TODO should notify if the new listener differs from old
    }

    /**
     * The country code (in ISO 3166 – alpha-3 format) of currently selected country
     * or null if not set.
     */
    var countryCode: String?
        /**
         * Get the country code of currently selected country or null if not set.
         *
         * @return the country code in ISO 3166 – alpha-3 format
         */
        get() = countryAutoCompleteTextView.country?.key3

        /**
         * Set current country by a country code. If value is null or blank clear the country text view.
         * If the value is not null or empty but cannot be found in the list adapter this method
         * will have no effect.
         *
         * @param value ISO 3166 – alpha-3 format
         */
        set(value) {
            countryAutoCompleteTextView.setCountryCode(value ?: "")
        }

    /**
     * The currently selected country or null if not set.
     */
    var country: Country?
        /**
         * Get the currently selected country or null if not set.
         */
        get() = countryAutoCompleteTextView.country

        /**
         * Set current country. If value is null clear the country text view.
         * If the value is not null but cannot be found in the list adapter this method
         * will have no effect.
         */
        set(value) {
            countryAutoCompleteTextView.country = value
        }

    /**
     * Street name and number
     */
    var street: String?
        /**
         * Get the street name and number.
         */
        get() = streetEditText.text?.toString()
        /**
         * Set street name and number.
         */
        set(value) {
            streetEditText.setText(value ?: "")
        }

    /**
     * City
     */
    var city: String?
        /**
         * Get the city.
         */
        get() = cityEditText.text?.toString()

        /**
         * Set city.
         */
        set(value) {
            cityEditText.setText(value ?: "")
        }

    /**
     * Postcode
     */
    var postCode: String?
        /**
         * Get the postcode.
         */
        get() = postCodeEditText.text?.toString()

        /**
         * Set postcode.
         */
        set(value) {
            postCodeEditText.setText(value ?: "")
        }

    /**
     * Add a text watcher to the Country input field.
     */
    fun setCountryTextWatcher(countryTextWatcher: TextWatcher) {
        countryAutoCompleteTextView.addTextChangedListener(countryTextWatcher)
    }

    /**
     * Add a text watcher to the Street input field.
     */
    fun setStreetTextWatcher(streetTextWatcher: TextWatcher) {
        streetEditText.addTextChangedListener(streetTextWatcher)
    }

    /**
     * Add a text watcher to the City input field.
     */
    fun setCityTextWatcher(cityTextWatcher: TextWatcher) {
        cityEditText.addTextChangedListener(cityTextWatcher)
    }

    /**
     * Add a text watcher to the Postcode input field.
     */
    fun setPostCodeTextWatcher(postCodeTextWatcher: TextWatcher) {
        postCodeEditText.addTextChangedListener(postCodeTextWatcher)
    }

    private fun countryLookup(countryName: String): Country? {
        val adapter = countryAutoCompleteTextView.adapter
        if (adapter != null) {
            return adapter.findCountryByDisplayText(countryName, context.localeLanguage)
        }

        return null
    }

    // State saving / restoration

    private val childrenToSave
        get() = mapOf<String, View>(
                "countryIL" to countryInputLayout,
                "countryACTV" to countryAutoCompleteTextView,
                "streetIL" to streetInputLayout,
                "streetET" to streetEditText,
                "cityIL" to cityInputLayout,
                "cityET" to cityEditText,
                "postCodeIL" to postCodeInputLayout,
                "postCodeET" to postCodeEditText,
        )

    private var viewIds: HashMap<String, Int>? = null

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState())
        if (viewIds == null) {
            childrenToSave.values.forEach { view -> view.id = generateViewId() }
            viewIds = HashMap<String, Int>(childrenToSave.mapValues { (key, view) -> view.id })
        }

        bundle.putSerializable(STATE_VIEW_IDS, viewIds)

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        viewIds = bundle.getSerializable(STATE_VIEW_IDS) as HashMap<String, Int>
        if (viewIds != null) {
            viewIds!!.forEach { (key, id) -> childrenToSave[key]!!.id = id }
        }
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE))
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        isRestoringViewState = true
        super.dispatchRestoreInstanceState(container)
        isRestoringViewState = false
    }

    companion object {
        private const val SUPER_INSTANCE_STATE = "saved_instance_state_parcelable"
        private const val STATE_VIEW_IDS = "state_view_ids"
    }
}