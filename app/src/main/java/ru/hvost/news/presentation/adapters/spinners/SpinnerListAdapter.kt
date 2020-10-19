package ru.hvost.news.presentation.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import ru.hvost.news.R
import ru.hvost.news.models.Species

class SpeciesSpinnerAdapter(
    context: Context,
    private val resource: Int
) : ArrayAdapter<Species>(context, resource) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        view.findViewById<TextView>(R.id.spinnerText)?.apply {
            text = getItem(position)?.speciesName
        }
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_view, parent, false)
        view.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        view.setPadding(0)
        view.findViewById<TextView>(R.id.spinnerText)?.apply {
            text = getItem(position)?.speciesName
        }
        return view
    }
}