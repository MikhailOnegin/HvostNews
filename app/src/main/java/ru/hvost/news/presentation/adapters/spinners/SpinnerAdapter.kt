package ru.hvost.news.presentation.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.hvost.news.R
import ru.hvost.news.presentation.customviews.ValueHolderView
import kotlin.reflect.KProperty1

class SpinnerAdapter<T> private constructor(
    context: Context,
    private val resource: Int,
    private val spinnerHint: String,
    private val property: KProperty1<T, String>
) : ArrayAdapter<T>(context, resource) {

    /**
     * @param context текущий контекст
     * @param spinnerHint подсказка для спиннера
     * @param collection список объектов для отображения в выпадающем списке
     * @param property свойство типа String элемента коллекции для отображения
     */
    constructor(
        context: Context,
        spinnerHint: String,
        collection: Collection<T>,
        property: KProperty1<T, String>
    ) : this(context, R.layout.spinner_dropdown_view, spinnerHint, property) {
        addAll(collection)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        view.findViewById<TextView>(R.id.spinnerText)?.apply {
            getItem(position)?.run {
                text = property.get(this)
            }
        }
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val valueHolder = (convertView ?: ValueHolderView(context)) as ValueHolderView
        valueHolder.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            context.resources.getDimension(R.dimen.widgetsHeight).toInt()
        )
        getItem(position)?.run {
            valueHolder.setSelection(property.get(this))
        }
        valueHolder.setHint(spinnerHint)
        return valueHolder
    }

}