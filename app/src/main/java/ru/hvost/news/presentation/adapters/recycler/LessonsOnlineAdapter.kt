package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lesson_online.view.*
import kotlinx.android.synthetic.main.item_lesson_online.view.textView_title
import ru.hvost.news.R
import ru.hvost.news.models.OnlineLessons

class LessonsOnlineAdapter() : RecyclerView.Adapter<LessonsOnlineAdapter.ViewHolder>() {

    private var lessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var onClickLesson: OnClickLesson? = null

    interface OnClickLesson {
        fun onClick(lesson: OnlineLessons.OnlineLesson)
    }

    fun setLessons(lessons: List<OnlineLessons.OnlineLesson>) {
        this.lessons = lessons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_online, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lesson = lessons[holder.adapterPosition]
        holder.bind(lesson)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumber = itemView.textView_number
        private val tvTitle = itemView.textView_title
        private val tVAge = itemView.textView_age
        private val ivGo = itemView.imageView_go

        fun bind(lesson: OnlineLessons.OnlineLesson) {
            tvNumber.text = lesson.lessonNumber.toString()
            if (lesson.lessonTitle.isNotBlank()) tvTitle.text = lesson.lessonTitle
            if (lesson.petAge.isNotBlank()) tVAge.text = lesson.petAge
            ivGo.setOnClickListener {
                onClickLesson?.onClick(lessons[adapterPosition])
            }
        }
    }
}