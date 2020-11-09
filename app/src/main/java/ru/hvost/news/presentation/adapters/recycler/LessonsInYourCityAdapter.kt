package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_lesson_offline_in_your_city.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OfflineLessons

class LessonsInYourCityAdapter : RecyclerView.Adapter<LessonsInYourCityAdapter.ViewHolder>() {
    private var lessonsFull = arrayListOf<OfflineLessons.OfflineLesson>()
    private var lessons = arrayListOf<OfflineLessons.OfflineLesson>()

    private var showFinished = true

    fun setLessons(lessons: List<OfflineLessons.OfflineLesson>) {
        this.lessonsFull = lessons.toCollection(ArrayList())
        this.lessons = lessons.toCollection(ArrayList())
        notifyDataSetChanged()
    }


    fun filter(showFinished:Boolean = this.showFinished){
       lessons = lessonsFull.filter { it.isFinished == showFinished }.toCollection(ArrayList())
        notifyDataSetChanged()
    }
    fun clear(){
        this.lessons = arrayListOf()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_offline_in_your_city, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.bind(lesson)
    }

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val iVLesson = itemView.imageView_lesson
        private val tVStatus = itemView.textView_lesson_status
        private val tVTitle = itemView.textViewLessonTitle
        private val tVDate = itemView.textView_lesson_date
        private val tVCity = itemView.textView_lesson_city

        fun bind(lesson:OfflineLessons.OfflineLesson){
            Glide.with(itemView.context).load(baseUrl + lesson.imageUrl).placeholder(R.drawable.not_found).centerCrop()
                .into(iVLesson)
            if(lesson.isFinished) {
                tVStatus.text = "Завершено"
                tVStatus.background = itemView.resources.getDrawable(R.drawable.background_coupon_status_true)
            }
            if(!lesson.isFinished) {
                tVStatus.text = "Активно"
                tVStatus.background = itemView.resources.getDrawable(R.drawable.background_coupon_staus_false)
            }
            tVTitle.text = lesson.title
            tVDate.text = lesson.date
            tVCity.text = lesson.city
        }
    }
}