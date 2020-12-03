package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lesson_online_active.view.*
import kotlinx.android.synthetic.main.item_lesson_online_active.view.constraint
import kotlinx.android.synthetic.main.item_lesson_online_active.view.textView_number
import kotlinx.android.synthetic.main.item_lesson_online_finished.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.textView_title
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools

class SchoolOnlineMaterialsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var school:OnlineSchools.OnlineSchool? = null
    private var lessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var onClickLessonActive: OnClickLessonActive? = null
    var onClickLessonFinished: OnClickLessonFinished? = null
    var onClickLiterature: OnClickLiterature? = null
    private var firstActiveLesson = true

    interface OnClickLessonActive {
        fun onClick(lessonId:String)
    }
    interface OnClickLessonFinished {
        fun onClick(lessonId:String)
    }

    interface OnClickLiterature{
        fun onClick(url:String)
    }

    fun setLessons(lessons: List<OnlineLessons.OnlineLesson>) {
        this.lessons = lessons.toCollection(ArrayList())
        this.firstActiveLesson = true
        notifyDataSetChanged()
    }

    fun setSchool(school: OnlineSchools.OnlineSchool){
        this.school = school
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewLessonActive = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_online_active, parent, false)

        val viewLessonFinished = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_online_finished, parent, false)

        val viewLiterature = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_useful_literature, parent, false)

        return when (viewType) {
            TYPE_LESSON_ACTIVE -> LessonActiveViewHolder(viewLessonActive)
            TYPE_LESSON_FINISHED -> LessonFinishedViewHolder(viewLessonFinished)
            else -> UsefulLiteratureViewHolder(viewLiterature)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= lessons.size) TYPE_USEFUL_LITERATURE
        else {
            if(!lessons[position].isFinished){
                TYPE_LESSON_ACTIVE
            } else{
                TYPE_LESSON_FINISHED
            }
        }
    }

    override fun getItemCount(): Int {
        return lessons.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LessonActiveViewHolder -> {
                val lesson = lessons[position]
                holder.bind(lesson)
            }
            is LessonFinishedViewHolder -> {
                val lesson = lessons[position]
                holder.bind(lesson)
            }
            is UsefulLiteratureViewHolder -> holder.bind(school)
        }
    }


    inner class LessonActiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val constraint = itemView.constraint
        private val tVNumber = itemView.textView_number
        private val tVTittle = itemView.textView_title
        private val tVAge = itemView.textView_age
        private val iVGo = itemView.imageView_play

        fun bind(lesson: OnlineLessons.OnlineLesson) {
            tVNumber.text = lesson.lessonNumber.toString()
            tVTittle.text = lesson.lessonTitle
            val age = "${itemView.resources.getString(R.string.age2)} ${lesson.petAge}"
            tVAge.text = age
            if (firstActiveLesson) {
                iVGo.visibility = View.VISIBLE
                constraint.setOnClickListener {
                    onClickLessonActive?.onClick(lesson.lessonId)
                }
                firstActiveLesson = false
            }

        }
    }

    inner class LessonFinishedViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        private val tVNumber = itemView.textView_number_finished
        private val tVTitle = itemView.textView_title_finished
        private val constraint = itemView.constraintFinished
        fun bind(lesson: OnlineLessons.OnlineLesson){
            tVNumber.text = lesson.lessonNumber.toString()
            tVTitle.text = lesson.lessonTitle
            constraint.setOnClickListener {
                onClickLessonFinished?.onClick(lesson.lessonId)
            }
        }
    }

    inner class UsefulLiteratureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(school: OnlineSchools.OnlineSchool?) {
            school?.run {
                val container = itemView.linearLayout_literature
                container.removeAllViews()
                for (i in school.literatures.indices) {
                    val view = LayoutLiteratureItemBinding.inflate(
                        LayoutInflater.from(itemView.context),
                        container,
                        false
                    ).root

                    view.textView_title.text = school.literatures[i].name
                    view.textView_pet.text = school.literatures[i].pet
                    view.constraint_literure.setOnClickListener {
                        onClickLiterature?.onClick(school.literatures[i].src)
                    }
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
        const val TYPE_LESSON_ACTIVE = 0
        const val TYPE_LESSON_FINISHED = 1
        const val TYPE_USEFUL_LITERATURE = 2
    }
}