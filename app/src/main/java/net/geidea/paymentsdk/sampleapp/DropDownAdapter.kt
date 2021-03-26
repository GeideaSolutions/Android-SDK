package net.geidea.paymentsdk.sampleapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

class DropDownAdapter<T>(
        context: Context,
        private val items: List<DropDownItem<T>>
) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): DropDownItem<T> = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getFilter(): Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence = resultValue.toString()
        override fun performFiltering(constraint: CharSequence?): FilterResults = FilterResults()
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.gd_item_country, parent, false).apply {
            val item = getItem(position)

            val textView = findViewById<TextView>(android.R.id.text1)
            textView.text = item.text
        }
    }
}