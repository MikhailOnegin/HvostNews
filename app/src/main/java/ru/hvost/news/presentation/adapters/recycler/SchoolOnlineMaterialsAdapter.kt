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
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools

class SchoolOnlineMaterialsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var school:OnlineSchools.OnlineSchool? = null
    private var lessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var onClickLesson: OnClickLesson? = null

    interface OnClickLesson {
        fun onClick(lessonId:String)
    }

    fun setLessons(lessons: List<OnlineLessons.OnlineLesson>) {
        this.lessons = lessons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun setSchool(school: OnlineSchools.OnlineSchool){
        this.school = school
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
        return if (position >= lessons.size) return TYPE_USEFUL_LITERATURE
        else TYPE_LESSON
    }

    override fun getItemCount(): Int {
        return lessons.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LessonsViewHolder -> {
                if (position < lessons.size) {
                    val lesson = lessons[position]
                    holder.bind(lesson)
                }
            }
            is UsefulLiteratureViewHolder ->
                    holder.bind(school)
        }
    }


    inner class LessonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val constraint = itemView.constraint
        private val tVNumber = itemView.textView_number
        private val tVTittle = itemView.textView_title
        private val tVAge = itemView.textView_age
        private val iVGo = itemView.imageView_go

        fun bind(lesson: OnlineLessons.OnlineLesson) {
            tVNumber.text = lesson.lessonNumber.toString()
            tVTittle.text = lesson.lessonTitle
            tVAge.text = lesson.petAge
            iVGo.setOnClickListener {
                //for Test
                Toast.makeText(itemView.context, "Click", Toast.LENGTH_SHORT).show()
            }
            constraint.setOnClickListener {
                onClickLesson?.onClick(lesson.lessonId)
            }

        }
    }

    inner class UsefulLiteratureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(school: OnlineSchools.OnlineSchool?) {
            school?.run {
                val container = itemView.linearLayout
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
                        margin,
                        0
                    )
                    container.addView(view)
                }
            }

        }
    }

    companion object {
        const val TYPE_LESSON = 0
        const val TYPE_USEFUL_LITERATURE = 1
    }
}