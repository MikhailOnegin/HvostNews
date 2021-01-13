package ru.hvost.news.presentation.adapters.recycler

import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.spec
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_what_wait_school_online.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.databinding.LayoutWhatWaitSchoolOnlineBinding
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
        private val includeLiterature = itemView.include_literature
        private val constraintWhatWait = itemView.constraint_what_wait

        fun bind(school: OnlineSchools.OnlineSchool){
            tVDescription.movementMethod = LinkMovementMethod()
            tVDescription.text = school.description.parseAsHtml()

            Glide.with(itemView.context).load(baseUrl + school.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(iVInfo)

            if(school.wait.isNotEmpty()) {

                constraintWhatWait.visibility = View.VISIBLE
                val gridLayoutWait = itemView.gridLayout
                gridLayoutWait.removeAllViews()
                for (i in school.wait.indices) {
                    val wait = school.wait[i]
                    val viewWait = LayoutWhatWaitSchoolOnlineBinding.inflate(
                        LayoutInflater.from(itemView.context),
                        gridLayoutWait,
                        false
                    ).root
                    val param = GridLayout.LayoutParams()
                    param.columnSpec = spec(
                        GridLayout.UNDEFINED,
                        GridLayout.FILL,
                        1f
                    )
                    param.width = 0
                    val margin = itemView.resources.getDimension(R.dimen.normalMargin).toInt() / 2

                    viewWait.layoutParams = param
                    viewWait.textView_head.text = wait.head.parseAsHtml()
                    viewWait.textView_description.text = wait.description.parseAsHtml()
                    Glide.with(itemView.context).load(baseUrl + wait.imageUrl)
                        .placeholder(R.drawable.not_found).centerCrop()
                        .into(viewWait.imageView_what_wait)
                    (viewWait.layoutParams as GridLayout.LayoutParams).setMargins(
                        margin,
                        margin,
                        margin,
                        margin
                    )
                    gridLayoutWait.addView(viewWait)
                }
            }


            if(school.literature.isNotEmpty()) {
                includeLiterature.visibility = View.VISIBLE
                val containerLiterature = itemView.linearLayout_literature
                containerLiterature.removeAllViews()
                for (i in school.literature.indices) {
                    val viewLiterature = LayoutLiteratureItemBinding.inflate(
                        LayoutInflater.from(itemView.context),
                        containerLiterature,
                        false
                    ).root

                    viewLiterature.textView_title.text = school.literature[i].title
                    viewLiterature.textView_pet.text = school.literature[i].pet
                    viewLiterature.constraint_literure.setOnClickListener {
                        onClickLiterature?.onClick(school.literature[i].fileUrl)
                    }
                    val margin = itemView.resources.getDimension(R.dimen.largeMargin).toInt()

                    if(i == school.literature.lastIndex) {
                        (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                            margin,
                            0,
                            margin,
                            0
                        )
                    }
                    else {
                        (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                            margin,
                            0,
                            0,
                            0
                        )
                    }
                    containerLiterature.addView(viewLiterature)
                }
            }
        }
    }
}