package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.spec
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_what_wait.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.databinding.LayoutWhatWaitBinding
import ru.hvost.news.models.OnlineSchools

class SchoolOnlineInfoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onlineSchool:OnlineSchools.OnlineSchool? = null
    var onClickLiterature:OnClickLiterature? = null

    interface OnClickLiterature{
        fun onClick(url:String)
    }

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
        private val tVDescription = itemView.textView_description_wait
        private val iVInfo = itemView.imageView_info
        fun bind(school: OnlineSchools.OnlineSchool){

            tVDescription.text = school.description

            Glide.with(itemView.context).load(APIService.baseUrl + school.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(iVInfo)

            val containerWait = itemView.gridLayout
            containerWait.removeAllViews()
            for (i in school.wait.indices) {
                val viewWait = LayoutWhatWaitBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    containerWait,
                    false
                ).root
                val param = GridLayout.LayoutParams()
                param.columnSpec = spec(
                    GridLayout.UNDEFINED,
                    GridLayout.FILL,
                    1f
                )
                param.width = 0
                viewWait.layoutParams = param
                viewWait.textView_section.text = school.wait[i].head
                viewWait.textView_description.text = school.literatures[i].pet
                containerWait.addView(viewWait)
            }

            val containerLiterature = itemView.linearLayout_literature
            containerLiterature.removeAllViews()
            for (i in school.literatures.indices) {
                val viewLiterature = LayoutLiteratureItemBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    containerLiterature,
                    false
                ).root

                viewLiterature.textView_title.text = school.literatures[i].name
                viewLiterature.textView_pet.text = school.literatures[i].pet
                viewLiterature.constraint_literure.setOnClickListener {
                    onClickLiterature?.onClick(school.literatures[i].src)
                }
                val margin = itemView.resources.getDimension(R.dimen.normalMargin).toInt()
                (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                    0,
                    margin,
                    margin,
                    0
                )
                containerLiterature.addView(viewLiterature)
            }


    }
}
}