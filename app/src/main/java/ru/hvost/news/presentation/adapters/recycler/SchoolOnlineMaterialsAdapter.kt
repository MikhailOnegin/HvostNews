package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_school_lesson_online_active.view.*
import kotlinx.android.synthetic.main.item_school_lesson_online_active.view.textView_number
import kotlinx.android.synthetic.main.item_school_lesson_online_finished.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.textView_title
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools

class SchoolOnlineMaterialsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var school:OnlineSchools.OnlineSchool? = null
    private var onlineLessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var onClickLessonActive: OnClickLessonActive? = null
    var onClickLessonFinished: OnClickLessonFinished? = null
    var onClickLiterature: OnClickLiterature? = null
    private var firstActiveLessonId:String? = null

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
        for(i in lessons.indices){
            val firstActiveLesson = lessons[i]
            if(!firstActiveLesson.isFinished){
                this.firstActiveLessonId = firstActiveLesson.lessonId
                break
            }
        }
        this.onlineLessons = lessons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun setSchool(school: OnlineSchools.OnlineSchool){
        this.school = school
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewLessonActive = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_lesson_online_active, parent, false)

        val viewLessonFinished = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_lesson_online_finished, parent, false)

        val viewLiterature = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_useful_literature, parent, false)

        return when (viewType) {
            TYPE_LESSON_ACTIVE -> LessonActiveViewHolder(viewLessonActive)
            TYPE_LESSON_FINISHED -> LessonFinishedViewHolder(viewLessonFinished)
            else -> UsefulLiteratureViewHolder(viewLiterature)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= onlineLessons.size) TYPE_USEFUL_LITERATURE
        else {
            if(!onlineLessons[position].isFinished){
                TYPE_LESSON_ACTIVE
            } else{
                TYPE_LESSON_FINISHED
            }
        }
    }

    override fun getItemCount(): Int {
        return onlineLessons.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LessonActiveViewHolder -> {
                val lesson = onlineLessons[position]
                holder.bind(lesson)
            }
            is LessonFinishedViewHolder -> {
                val lesson = onlineLessons[position]
                holder.bind(lesson)
            }
            is UsefulLiteratureViewHolder -> holder.bind(school)
        }
    }


    inner class LessonActiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val constraint = itemView.constraint_lesson
        private val tVNumber = itemView.textView_number
        private val tVTittle = itemView.textView_title
        private val tVAge = itemView.textView_age
        private val iVGo = itemView.imageView_play

        fun bind(lesson: OnlineLessons.OnlineLesson) {
            tVNumber.text = lesson.lessonNumber.toString()
            tVTittle.text = lesson.lessonTitle
            if(lesson.petAge.isNotBlank()){
                val age = "${itemView.resources.getString(R.string.age2)} ${lesson.petAge}"
                tVAge.text = age
            }
            firstActiveLessonId?.run {
                if (this == lesson.lessonId) {
                    iVGo.visibility = View.VISIBLE
                    constraint.setOnClickListener {
                        onClickLessonActive?.onClick(lesson.lessonId)
                    }
                }
            }


        }
    }

    inner class LessonFinishedViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        private val tVNumber = itemView.textView_number_finished
        private val tVTitle = itemView.textView_title_finished
        private val constraint = itemView.constraintlessonFinished
        fun bind(lesson: OnlineLessons.OnlineLesson){
            tVNumber.text = lesson.lessonNumber.toString()
            tVTitle.text = lesson.lessonTitle
            constraint.setOnClickListener {
                onClickLessonFinished?.onClick(lesson.lessonId)
            }
        }
    }

    inner class UsefulLiteratureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val constraintRoot = itemView.constraint_root

        fun bind(school: OnlineSchools.OnlineSchool?) {
            school?.run {
                if (school.literature.isNotEmpty()) {

                    val container = itemView.linearLayout_literature
                    container.removeAllViews()
                    for (i in school.literature.indices) {
                        val view = LayoutLiteratureItemBinding.inflate(
                            LayoutInflater.from(itemView.context),
                            container,
                            false
                        ).root

                        view.textView_title.text = school.literature[i].title
                        view.textView_pet.text = school.literature[i].pet
                        view.constraint_literure.setOnClickListener {
                            onClickLiterature?.onClick(school.literature[i].fileUrl)
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
                else{
                   constraintRoot.visibility = View.GONE
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