package ru.hvost.news.presentation.adapters.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.adapters.view.GridViewWaitAdapter

class SchoolOnlineInfoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onlineSchool:OnlineSchools.OnlineSchool? = null

    fun setSchool(school:OnlineSchools.OnlineSchool){
        this.onlineSchool = school
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_online_info, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){

           is MainViewHolder -> {onlineSchool?.run {
               holder.bind(this)
           }
           }
        }
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tVDescription = itemView.textView_description
        private val gridView = itemView.gridView
        private val iVInfo = itemView.imageView_info
        fun bind(school: OnlineSchools.OnlineSchool){

            tVDescription.text = school.description
            Glide.with(itemView.context).load(APIService.baseUrl + school.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(iVInfo)
            val gridAdapter = GridViewWaitAdapter(itemView.context, school.wait)
            gridView.adapter = gridAdapter
            val container = itemView.include_literature.linearLayout
            container.removeAllViews()
            for (i in school.literatures.indices) {
                val view = LayoutLiteratureItemBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    container,
                    false
                ).root

                view.textView_title.text = school.literatures[i].name
                view.textView_pet.text = school.literatures[i].pet
                val margin = itemView.resources.getDimension(R.dimen.normalMargin).toInt()
                (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                    0,
                    margin,
                    margin + margin,
                    0
                )
                container.addView(view)
            }
        }

    }
}