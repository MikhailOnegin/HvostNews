package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lesson_online.view.*
import kotlinx.android.synthetic.main.item_lesson_online.view.textView_title
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons

class SchoolOnlineMaterialsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lessons = arrayListOf<OnlineLessons.OnlineLesson>()
    private var literature = arrayListOf<OnlineSchoolsResponse.Literature>()
    var onClickLesson:OnClickLesson? = null
    interface OnClickLesson {
       fun  onClick()
    }

    fun setLessons(lessons:List<OnlineLessons.OnlineLesson>){
        this.lessons = lessons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun setLiterature(literature:List<OnlineSchoolsResponse.Literature>){
        this.literature = literature.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewSchool = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_online, parent, false)

        val viewLiterature = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_useful_literature, parent, false)

        return when (viewType) {
            TYPE_LESSON -> LessonsViewHolder(viewSchool)
            else -> UsefulLiteratureViewHolder(viewLiterature)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position >= lessons.size) return TYPE_USEFUL_LITERATURE
        else TYPE_LESSON
    }

    override fun getItemCount(): Int {
        return lessons.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is LessonsViewHolder -> {
                if(position<lessons.size) {
                    val lesson = lessons[position]
                    holder.bind(lesson)
                }
            }
            is UsefulLiteratureViewHolder ->
                holder.bind(literature)
        }
    }


    inner class LessonsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val constraint = itemView.constraint
        private val tVNumber = itemView.textView_number
        private val tVTittle = itemView.textView_title
        private val tVAge = itemView.textView_age
        private val iVGo = itemView.imageView_go

        fun bind(lesson: OnlineLessons.OnlineLesson){
            tVNumber.text = lesson.lessonNumber.toString()
            tVTittle.text = lesson.lessonTitle
            tVAge.text = lesson.petAge
            iVGo.setOnClickListener {
                Toast.makeText(itemView.context, "Click", Toast.LENGTH_SHORT).show()
            }
            constraint.setOnClickListener {
                onClickLesson?.onClick()
            }

        }
    }

    inner class UsefulLiteratureViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(literature:List<OnlineSchoolsResponse.Literature>){
            val container = itemView.linearLayout
            container.removeAllViews()
            for(i in literature.indices){
                val view = LayoutLiteratureItemBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    container,
                    false
                ).root

                view.textView_title.text = literature[i].title
                view.textView_pet.text = literature[i].pet
                val margin = itemView.resources.getDimension(R.dimen.normalMargin).toInt()
                (view.layoutParams as LinearLayout.LayoutParams).setMargins(0, margin, margin + margin, 0)
                container.addView(view)
            }
        }
    }

    companion object {
        const val TYPE_LESSON = 0
        const val TYPE_USEFUL_LITERATURE = 1
    }
}