package ru.hvost.news.presentation.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.hvost.news.R
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.customviews.ValueHolderView

class SpeciesSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val spinnerHint: String
) : ArrayAdapter<Species>(context, resource) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        view.findViewById<TextView>(R.id.spinnerText)?.apply {
            text = getItem(position)?.speciesName
        }
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val valueHolder = (convertView ?: ValueHolderView(context)) as ValueHolderView
        valueHolder.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            context.resources.getDimension(R.dimen.widgetsHeight).toInt()
        )
        valueHolder.setSelection(getItem(position)?.speciesName)
        valueHolder.setHint(spinnerHint)
        return valueHolder
    }
}