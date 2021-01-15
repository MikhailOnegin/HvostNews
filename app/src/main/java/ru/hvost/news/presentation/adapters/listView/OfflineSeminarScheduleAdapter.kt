package ru.hvost.news.presentation.adapters.listView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutOfflineSemianrScheduleBinding
import ru.hvost.news.models.OfflineSeminars

class OfflineSeminarScheduleAdapter(context: Context, resources: Int, val items: List<OfflineSeminars.Schedule>) :
        ArrayAdapter<OfflineSeminars.Schedule>(context, resources, items) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =  LayoutOfflineSemianrScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val schedule = items[position]
        val time = "${schedule.timeStart} ${schedule.timeFinish}"
        view.textViewTime.text = time
        view.textViewDescription.text = schedule.description
        view.textViewDate.text = schedule.date
        return  view.root
    }
}