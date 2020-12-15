package ru.hvost.news.presentation.adapters.autocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import ru.hvost.news.R
import ru.hvost.news.models.Shop

class AutoCompleteShopsAdapter(
    context: Context,
    private val resource: Int,
    private val list: List<Shop>
) : ArrayAdapter<Shop>(context, resource, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val title = view.findViewById<TextView>(R.id.title)
        val subtitle = view.findViewById<TextView>(R.id.subtitle)
        title.text = getItem(position)?.name
        subtitle.text = getItem(position)?.address
        return view
    }

    override fun getFilter(): Filter {
        return CustomFilter(list.toMutableList())
    }

    inner class CustomFilter(private val fullList: List<Shop>) : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = fullList.filter {
                    it.name.contains(constraint ?: "", true)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            addAll(results?.values as List<Shop>)
            if (results.count > 0) notifyDataSetChanged()
            else notifyDataSetInvalidated()
        }

    }

}