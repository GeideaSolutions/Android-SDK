package net.geidea.paymentsdk.ui.widget.address

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.LocaleUtils.localeLanguage
import net.geidea.paymentsdk.model.Country

/**
 * Default implementation of a simple list adapter for populating the exposed Material drop-down menu
 * of [CountryAutoCompleteTextView]. Country names will be localized based on the context locale.
 */
open class DefaultCountryDropDownAdapter(
        context: Context,
        val countries: List<Country>
) : BaseAdapter(), Filterable {

    private val localeLanguage: String = context.localeLanguage

    /**
     * List of countries which are supported by the Merchant.
     */
    val supportedCountries: List<Country> = countries.filter(Country::isSupported)

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = supportedCountries.size
    override fun getItem(position: Int): Country = supportedCountries[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getFilter(): Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? Country?)?.let { getCountryDisplayText(it, localeLanguage) } ?: ""
        }
        override fun performFiltering(constraint: CharSequence?): FilterResults = FilterResults()
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.gd_item_single_text, parent, false).apply {
            val item = getItem(position)

            val textView = findViewById<TextView>(android.R.id.text1)
            textView.text = getCountryDisplayText(item, localeLanguage)
        }
    }
}

/**
 * The name of the [country] to be displayed in the menu item.
 */
internal fun getCountryDisplayText(country: Country, localeLanguage: String): CharSequence {
    return country.getLocalizedName(localeLanguage) ?: ""
}

internal fun Adapter.findCountryByDisplayText(displayText: String, localeLanguage: String): Country? {
    (0 until count).forEach { i ->
        val country = getItem(i) as Country
        if (country.isSupported && getCountryDisplayText(country, localeLanguage) == displayText) {
            return country
        }
    }

    return null
}