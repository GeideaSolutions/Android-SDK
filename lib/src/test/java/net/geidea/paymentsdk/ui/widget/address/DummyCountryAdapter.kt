package net.geidea.paymentsdk.ui.widget.address

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import io.mockk.mockk
import net.geidea.paymentsdk.model.Country

class DummyCountryAdapter(vararg val countries: Country) : BaseAdapter(), Filterable {
    override fun getCount(): Int = countries.size
    override fun getItem(position: Int): Country = countries[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getFilter(): Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? Country?)?.let(::getCountryDisplayText) ?: ""
        }
        override fun performFiltering(constraint: CharSequence?): FilterResults = FilterResults()
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View = mockk()
    fun getCountryDisplayText(country: Country): CharSequence = country.nameEn ?: ""
}