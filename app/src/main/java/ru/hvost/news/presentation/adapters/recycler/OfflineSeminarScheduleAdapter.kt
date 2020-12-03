package ru.hvost.news.presentation.adapters.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_offline_seminar_schedule.view.*
import kotlinx.android.synthetic.main.layout_partner.view.*
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutPartnerBinding
import ru.hvost.news.models.OfflineSeminars

class OfflineSeminarScheduleAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var seminar: OfflineSeminars.OfflineLesson? = null

    fun setSeminar(seminar:OfflineSeminars.OfflineLesson){
        this.seminar = seminar
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offline_seminar_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ScheduleViewHolder) holder.bind(seminar)
    }

    inner class ScheduleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(seminar:OfflineSeminars.OfflineLesson?){
            seminar?.run {
                Log.i("eeee", "seminar Schedule is not null")
            val containerWait = itemView.gridLayout
            containerWait.removeAllViews()
            for (i in seminar.partners.indices) {
                val partner = seminar.partners[i]
                val viewPartner = LayoutPartnerBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    containerWait,
                    false
                ).root
                val param = GridLayout.LayoutParams()
                param.columnSpec = GridLayout.spec(
                    GridLayout.UNDEFINED,
                    GridLayout.FILL,
                    1f
                )
                param.width = 0
                viewPartner.layoutParams = param
                val marginNormal = itemView.resources.getDimension(R.dimen.normalMargin).toInt()
                val marginSmall = itemView.resources.getDimension(R.dimen.partnerSmallPadding).toInt()
                (viewPartner.layoutParams as GridLayout.LayoutParams).setMargins(
                    0,
                    marginSmall,
                    marginNormal,
                    0
                )
                viewPartner.textView_partner_title.text = partner.title
                containerWait.addView(viewPartner)
                Glide.with(itemView.context).load(partner.imageUrl)
                    .placeholder(R.drawable.not_found).centerCrop()
                    .into(viewPartner.imageView)
            }
            }
        }
    }
}