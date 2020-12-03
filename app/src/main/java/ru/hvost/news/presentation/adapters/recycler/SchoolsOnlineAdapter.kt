package ru.hvost.news.presentation.adapters.recycler


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online.view.*
import kotlinx.android.synthetic.main.layout_lesson_number.view.*
import kotlinx.android.synthetic.main.layout_lesson_numbers.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.LayoutLessonNumberBinding
import ru.hvost.news.databinding.LayoutLessonNumbersBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools

class SchoolsOnlineAdapter : RecyclerView.Adapter<SchoolsOnlineAdapter.SchoolsViewHolder>() {

    private var schools = arrayListOf<OnlineSchools.OnlineSchool>()
    var clickSchool: ClickSchool? = null

    interface ClickSchool {
        fun onClick(schoolId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_school_online, parent, false)
        return SchoolsViewHolder(view)
    }


    fun setSchools(onlineSchools: List<OnlineSchools.OnlineSchool>) {
        this.schools = onlineSchools.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    override fun onBindViewHolder(holder: SchoolsViewHolder, position: Int) {
        val item = schools[position]
        holder.bind(item)
    }


    inner class SchoolsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clSchool = itemView.constraint_school
        private val ivSchool = itemView.imageView_school
        private val tvTitle = itemView.textView_title
        private val tvRank = itemView.textView_rank


        fun bind(onlineSchool: OnlineSchools.OnlineSchool) {
            if (onlineSchool.title.isNotBlank()) tvTitle.text = onlineSchool.title
            if (onlineSchool.userRank.isNotBlank()) tvRank.text = onlineSchool.userRank
            Glide.with(itemView.context).load(baseUrl + onlineSchool.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(ivSchool)
            clSchool.setOnClickListener {
                clickSchool?.onClick(onlineSchool.id.toString())
            }

            val containerWait = itemView.linearLayout_lesson_numbers
            containerWait.removeAllViews()
            for(i in onlineSchool.lessonsPassed.indices){
                val number = onlineSchool.lessonsPassed[i].number
                val isPassed = onlineSchool.lessonsPassed[i].isPassed
                    val viewWait = LayoutLessonNumberBinding.inflate(
                        LayoutInflater.from(itemView.context),
                        containerWait,
                        false
                    ).root

                viewWait.textView_lesson_number.text = number.toString()
                viewWait.textView_lesson_number.isSelected = isPassed
                if (isPassed) viewWait.textView_lesson_number.setTextColor(itemView.resources.getColor(R.color.gray3))
                else viewWait.textView_lesson_number.setTextColor(itemView.resources.getColor(android.R.color.white))

                val margin = itemView.resources.getDimension(R.dimen.marginLessonNumber).toInt()
                (viewWait.layoutParams as LinearLayout.LayoutParams).setMargins(
                    0,
                    0,
                    margin,
                    0
                )
                containerWait.addView(viewWait)
            }
        }
    }
}