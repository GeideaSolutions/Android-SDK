package net.geidea.paymentsdk.ui.validation.address.validator

import android.widget.AutoCompleteTextView
import net.geidea.paymentsdk.model.Country
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.address.reason.CountriesNotLoaded
import net.geidea.paymentsdk.ui.validation.address.reason.InvalidCountry

/**
 * Validator for address country.
 *
 * Possible invalidation reasons:
 * - [InvalidCountry]
 * - [CountriesNotLoaded]
 *
 * @param areCountriesLoadedFun predicate called to check if accepted countries are loaded
 * @param countryLookupFun function that must return a country by its name (display text).
 * @param validatorFun function which determines if a country in the list of loaded countries is valid.
 * By default always returns [ValidationStatus.Valid]. Supply custom function to have additional
 * custom validation.
 */
open class DefaultCountryValidator @JvmOverloads constructor(
        /**
         * Predicate called to check if accepted countries are loaded
         */
        private val areCountriesLoadedFun: () -> Boolean,

        /**
         * Function that must return a country by its name (display text).
         */
        private val countryLookupFun: (String) -> Country?,

        /**
         * Function which determines if a country in the list of loaded countries is valid.
         * By default always returns [ValidationStatus.Valid].
         * Supply custom function to have additional custom validation.
         */
        private val validatorFun: (Country) -> ValidationStatus = { ValidationStatus.Valid },

) : Validator<String>, AutoCompleteTextView.Validator {

    override fun validate(value: String?): ValidationStatus {
        if (value.isNullOrEmpty()) {
            return ValidationStatus.Valid
        }

        if (!areCountriesLoadedFun()) {
            return ValidationStatus.Invalid(CountriesNotLoaded)
        }

        val foundCountry: Country? = countryLookupFun(value)
        return if (foundCountry != null) {
            validatorFun(foundCountry)
        } else {
            ValidationStatus.Invalid(InvalidCountry)
        }
    }

    override fun isValid(text: CharSequence?): Boolean =
            validate(text?.toString()) == ValidationStatus.Valid

    override fun fixText(invalidText: CharSequence?): CharSequence {
        return ""   // Nothing to do to fix it except to clear it
    }
}