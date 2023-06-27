package net.geidea.paymentsdk.ui.widget.address

import android.content.Context
import android.os.Build
import android.text.Editable
import android.util.AttributeSet
import android.view.autofill.AutofillValue
import android.widget.Filterable
import android.widget.ListAdapter
import androidx.annotation.RequiresApi
import androidx.autofill.HintConstants
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.LocaleUtils.localeLanguage
import net.geidea.paymentsdk.model.Country
import net.geidea.paymentsdk.ui.validation.OnInvalidStatusListener
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.Validatable
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.widget.OnErrorListener
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter

/**
 * Country input text field used with conjunction with exposed drop-down menu.
 *
 * Use [setAdapter] to populate the drop-down menu.
 *
 * @see DefaultCountryDropDownAdapter
 */
open class CountryAutoCompleteTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.autoCompleteTextViewStyle
) : MaterialAutoCompleteTextView(context, attrs, defStyleAttr), Validatable<String, Country?> {

    private val localeLanguage: String = context.localeLanguage

    /**
     * Returns true if the country is valid (no country selected is considered valid case).
     *
     * That is, the current text is a name of a country in
     * the adapter. Blank text is considered valid.
     */
    override val isValid: Boolean get() = validationStatus == ValidationStatus.Valid

    /**
     * Set a country validator.
     */
    fun setCountryValidator(validator: net.geidea.paymentsdk.ui.validation.Validator<String>) {
        _validator = validator
        // TODO should we also set it with AutoCompleteTextView.setValidator() ?
    }

    override fun validate() {
        _validator?.let {
            val displayText = text?.toString() ?: ""
            validationStatus = it.validate(displayText)
            country = if (displayText.isNotEmpty()) {
                findCountryByDisplayText(displayText)
            } else {
                null
            }
        }
    }

    override var validationStatus: ValidationStatus = ValidationStatus.Undefined
        internal set(status) {
            if (field != status) {
                field = status
                val newCountry: Country? = displayTextToCountry(text?.toString() ?: "")
                when (status) {
                    ValidationStatus.Valid -> {
                        errorMessage = null
                        onValidStatusListener?.onValidStatus(newCountry)
                    }
                    is ValidationStatus.Invalid -> onInvalidStatusListener?.onInvalidStatus(newCountry, status)
                    is ValidationStatus.Undefined -> error("Cannot assign ValidationStatus.Undefined")
                }
            }
        }

    private var onValidStatusListener: OnValidStatusListener<Country?>? = null
    private var onInvalidStatusListener: OnInvalidStatusListener<Country?>? = null

    protected var _validator: net.geidea.paymentsdk.ui.validation.Validator<String>? = null
        private set(value) {
            if (field != value) {
                field = value
                if (value != null) {
                    validate()
                }
            }
        }

    private val validationTextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable) {
            validate()
        }
    }

    /**
     * The current validation error message associated with this text input field.
     * Null if the field is in a valid state.
     */
    var errorMessage: CharSequence? = null
        set(value) {
            if (field != value) {
                field = value
                onErrorListener?.onShowError(value)
            }
        }

    override var isErrorShownOnFocusLost: Boolean = true

    private var onErrorListener: OnErrorListener? = null
    private var focusChangeListener: OnFocusChangeListener? = null

    init {
        addTextChangedListener(validationTextWatcher)
        super.setOnFocusChangeListener { view, hasFocus ->
            if (isErrorShownOnFocusLost && !hasFocus) {
                updateErrorMessage()
            }
            focusChangeListener?.onFocusChange(view, hasFocus)
        }
    }

    override fun setOnValidStatusListener(listener: OnValidStatusListener<Country?>?) {
        if (onValidStatusListener != listener) {
            onValidStatusListener = listener
            if (validationStatus == ValidationStatus.Valid) {
                onValidStatusListener?.onValidStatus(country)
            }
        }
    }

    override fun setOnInvalidStatusListener(listener: OnInvalidStatusListener<Country?>?) {
        if (onInvalidStatusListener != listener) {
            onInvalidStatusListener = listener
            val status = validationStatus
            if (status is ValidationStatus.Invalid) {
                onInvalidStatusListener?.onInvalidStatus(country, status)
            }
        }
    }

    override fun updateErrorMessage() {
        val status = validationStatus
        errorMessage = if (status is ValidationStatus.Invalid)
            status.reason.getMessage(context)
        else
            null
    }

    override fun setOnErrorListener(listener: OnErrorListener) {
        this.onErrorListener = listener
    }

    override fun setOnFocusChangeListener(listener: OnFocusChangeListener?) {
        this.focusChangeListener = listener
    }

    /**
     * Set list adapter for the country drop-down menu. **Must** contain supported countries as items of
     * type [Country]. Triggers validation.
     */
    override fun <T> setAdapter(adapter: T?) where T : ListAdapter?, T : Filterable? {
        if (adapter != null) {
            repeat(adapter.count) {
                val item = adapter.getItem(it)
                if (item is Country) {
                    require(item.isSupported) { "Adapter items be of type Country with isSupported=true" }
                }
            }
        }

        val hadAdapter = this.adapter != null
        super.setAdapter(adapter)
        validate()

        if (hadAdapter) {
            // Show/hide error only when replacing adapter
            updateErrorMessage()
        }
    }

    /**
     * Sets the country by its country code in ISO 3166 – alpha-3 format.
     *
     * @param countryCode if null or blank then the text of this view will cleared.
     * @throws IllegalArgumentException if adapter is set but [countryCode]
     * cannot be found in the adapter then method
     */
    fun setCountryCode(countryCode: String?) {
        if (countryCode.isNullOrBlank()) {
            setText("", false)
        } else if (adapter != null) {
            val foundCountry: Country = requireNotNull(findCountryBy3LetterCode(countryCode)) {
                "No countryCode $countryCode found"
            }
            setText(countryToDisplayText(foundCountry), false)
        }
    }

    /**
     * Currently selected [country][Country] or null if none is selected.
     */
    var country: Country? = null
        /**
         * Set current [country][Country] or null to clear the text.
         */
        set(value) {
            if (field != value) {
                field = value
                setText(countryToDisplayText(value), false)
                onCountryChangedListener?.onCountryChanged(value)
            }
        }

    private fun countryToDisplayText(country: Country?): String {
        return when (localeLanguage) {
            "ar" -> country?.nameAr ?: country?.nameEn ?: ""
            "en" -> country?.nameEn ?: ""
            else -> country?.nameEn ?: ""
        }
    }

    private fun displayTextToCountry(displayText: String): Country? {
        return findCountryByDisplayText(displayText)
    }

    private fun findCountryByDisplayText(displayText: String): Country? {
        return findCountryBy { country -> displayText.equals(countryToDisplayText(country), ignoreCase = true) }
    }

    private fun findCountryBy3LetterCode(code3: String): Country? {
        return findCountryBy { country -> code3.equals(country.key3, ignoreCase = true) }
    }

    private fun findCountryBy2LetterCode(code2: String): Country? {
        return findCountryBy { country -> code2.equals(country.key2, ignoreCase = true) }
    }

    private fun findCountryBy(predicate: (Country) -> Boolean): Country? {
        if (adapter != null) {
            repeat(adapter.count) { i ->
                val item = adapter.getItem(i)
                if (item is Country && predicate(item)) {
                    return item
                }
            }
        }

        return null
    }

    private var onCountryChangedListener: OnCountryChangedListener? = null

    /**
     * Set listener to be notified when current [country][Country] is changed.
     */
    fun setOnCountryChangedListener(listener: OnCountryChangedListener?) {
        if (this.onCountryChangedListener !== listener) {
            this.onCountryChangedListener = listener
            this.onCountryChangedListener?.onCountryChanged(country)
        }
    }

    // Autofill support

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAutofillType(): Int {
        return AUTOFILL_TYPE_TEXT
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAutofillValue(): AutofillValue? {
        return text?.let(AutofillValue::forText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun autofill(value: AutofillValue?) {
        if (value == null || !value.isText) {
            return
        }
        if (adapter == null) {
            setText(value.textValue, false)
        } else {
            val string: String = value.textValue.toString()
            this.country = findCountryBy2LetterCode(string)
                    ?: findCountryBy3LetterCode(string)
        }
    }

    override fun getAutofillHints(): Array<String> {
        return AUTOFILL_HINT
    }

    companion object {
        val AUTOFILL_HINT = arrayOf(HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_COUNTRY)
    }
}