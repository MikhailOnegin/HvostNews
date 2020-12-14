package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.*
import kotlinx.android.synthetic.main.layout_partner.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.LayoutPartnerBinding
import ru.hvost.news.models.OfflineSeminars

class OfflineSeminarInfoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var seminar:OfflineSeminars.OfflineLesson? = null

    fun setSeminar(seminar:OfflineSeminars.OfflineLesson){
        this.seminar = seminar
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_school_offline_seminar_info_partners, parent, false)
        return InfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InfoViewHolder) holder.bind(seminar)
    }

    inner class InfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(seminar:OfflineSeminars.OfflineLesson?){
            seminar?.run {
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
                    viewPartner.textView_partner_title.text = partner.name
                    containerWait.addView(viewPartner)
                    Glide.with(itemView.context).load(baseUrl + partner.image)
                        .placeholder(R.drawable.not_found).centerCrop()
                        .into(viewPartner.imageView)
                }
            }


        }
    }

}