package ru.hvost.news.presentation.adapters.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_offline_seminar_info.view.gridLayout
import kotlinx.android.synthetic.main.layout_partner.view.*
import kotlinx.android.synthetic.main.layout_partner.view.imageView
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutPartnerBinding
import ru.hvost.news.models.OfflineSeminars

class OfflineSeminarInfoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var seminar:OfflineSeminars.OfflineLesson? = null

    fun setSeminar(seminar:OfflineSeminars.OfflineLesson){
        this.seminar = seminar
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offline_seminar_info, parent, false)
        return InfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InfoViewHolder) holder.bind()
    }

    inner class InfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        fun bind(){
                for (i  in 0 .. 10) {
                    Log.i("eeee", "partners size")
                }

        }

    }

}