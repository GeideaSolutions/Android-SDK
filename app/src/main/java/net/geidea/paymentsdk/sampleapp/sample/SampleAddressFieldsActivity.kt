package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleAddressFieldsBinding
import net.geidea.paymentsdk.sampleapp.showObjectAsJson
import net.geidea.paymentsdk.sampleapp.snack
import net.geidea.paymentsdk.ui.validation.FieldMaxLengthValidator
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidCityLength
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidPostCodeLength
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidStreetLength
import net.geidea.paymentsdk.ui.validation.address.validator.DefaultCountryValidator
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener
import net.geidea.paymentsdk.ui.widget.address.DefaultCountryDropDownAdapter

class SampleAddressFieldsActivity : BaseSampleActivity<ActivitySampleAddressFieldsBinding>() {

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleAddressFieldsBinding {
        return ActivitySampleAddressFieldsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Address input fields sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        // Country

        val countryList = merchantConfig.countries?.toList() ?: emptyList()
        val countriesAdapter = DefaultCountryDropDownAdapter(this@SampleAddressFieldsActivity, countryList)
        countryAutoCompleteTextView.setAdapter(countriesAdapter)
        val countryValidator = DefaultCountryValidator(
                areCountriesLoadedFun = { countryAutoCompleteTextView.adapter != null },
                countryLookupFun = { countryName ->
                    countriesAdapter.supportedCountries.first { country -> country.nameEn == countryName }
                }
        )
        countryAutoCompleteTextView.setCountryValidator(countryValidator)
        countryAutoCompleteTextView.setOnErrorListener(TextInputErrorListener(countryInputLayout))
        countryAutoCompleteTextView.setOnCountryChangedListener { country ->
            snack("Country changed: value=${country?.key3}")
        }
        countryAutoCompleteTextView.setOnValidStatusListener {
            updateValidationStatus(countryStatusTextView, ValidationStatus.Valid)
        }
        countryAutoCompleteTextView.setOnInvalidStatusListener { value, status ->
            updateValidationStatus(countryStatusTextView, status)
            countryAutoCompleteTextView.updateErrorMessage()
        }

        // Street

        streetEditText.setValidator(FieldMaxLengthValidator { InvalidStreetLength })
        streetEditText.setOnErrorListener(TextInputErrorListener(streetInputLayout))
        streetEditText.setOnValidStatusListener {
            updateValidationStatus(streetStatusTextView, ValidationStatus.Valid)
        }
        streetEditText.setOnInvalidStatusListener { value, status ->
            updateValidationStatus(streetStatusTextView, status)
            streetEditText.updateErrorMessage()
        }

        // City

        cityEditText.setValidator(FieldMaxLengthValidator { InvalidCityLength })
        cityEditText.setOnErrorListener(TextInputErrorListener(cityInputLayout))
        cityEditText.setOnValidStatusListener {
            updateValidationStatus(cityStatusTextView, ValidationStatus.Valid)
        }
        cityEditText.setOnInvalidStatusListener { value, status ->
            updateValidationStatus(cityStatusTextView, status)
            cityEditText.updateErrorMessage()
        }

        // Postcode

        postCodeEditText.setValidator(FieldMaxLengthValidator { InvalidPostCodeLength })
        postCodeEditText.setOnErrorListener(TextInputErrorListener(postCodeInputLayout))
        postCodeEditText.setOnValidStatusListener {
            updateValidationStatus(postCodeStatusTextView, ValidationStatus.Valid)
        }
        postCodeEditText.setOnInvalidStatusListener { value, status ->
            updateValidationStatus(postCodeStatusTextView, status)
            postCodeEditText.updateErrorMessage()
        }

        // Get Address button

        getAddressButton.setOnClickListener {
            showObjectAsJson(getAddress()) {
                setTitle("Address (as JSON)")
            }
        }
    }

    private fun getAddress(): Address = with(binding) {
        Address(
                countryCode = countryAutoCompleteTextView.country?.key3 ?: null,
                street = streetEditText.text?.toString(),
                city = cityEditText.text?.toString(),
                postCode = postCodeEditText.text?.toString()
        )
    }

    /**
     * Function that displays a validation status in a TextView for test/demo/debug purposes.
     */
    @SuppressLint("SetTextI18n")
    private fun updateValidationStatus(statusTextView: TextView, status: ValidationStatus) = with(binding) {
        statusTextView.text = when (status) {
            ValidationStatus.Undefined -> "Undefined"
            ValidationStatus.Valid -> "Valid"
            is ValidationStatus.Invalid -> status.reason.javaClass.simpleName
        }

        getAddressButton.isEnabled = allFieldsValid()
    }

    private fun allFieldsValid(): Boolean = with(binding) {
        return countryAutoCompleteTextView.isValid &&
                streetEditText.isValid &&
                cityEditText.isValid &&
                postCodeEditText.isValid
    }
}