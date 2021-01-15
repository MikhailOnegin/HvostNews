package ru.hvost.news.presentation.adapters.listView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.hvost.news.databinding.LayoutOfflineSemianrScheduleBinding
import ru.hvost.news.models.OfflineSeminars

class OfflineSeminarScheduleAdapter(context: Context, val resources: Int, val items: List<OfflineSeminars.Schedule>) :
        ArrayAdapter<OfflineSeminars.Schedule>(context, resources, items) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = LayoutOfflineSemianrScheduleBinding.inflate(
                LayoutInflater.from(context),
                parent, false
        )
        binding.textViewDate.text = items[position].date
        binding.textViewDescription.text = items[position].description
        val time = "${items[position].timeStart} ${items[position].timeFinish}"
        binding.textViewTime.text = time
        return binding.root
    }
}