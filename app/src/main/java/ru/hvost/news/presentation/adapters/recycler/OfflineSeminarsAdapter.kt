package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_lesson_offline_in_your_city.view.*
import kotlinx.android.synthetic.main.item_school_offline_spinner.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter

class OfflineSeminarsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var lessonsFull = arrayListOf<OfflineSeminars.OfflineLesson>()
    private var lessons = arrayListOf<OfflineSeminars.OfflineLesson>()
    private var spinnerAdapter:SpinnerAdapter<String>? = null

    private var showFinished = true

    fun setSeminars(seminars: List<OfflineSeminars.OfflineLesson>) {
        this.lessonsFull = seminars.toCollection(ArrayList())
        this.lessons = seminars.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun setSpinnerAdapter(adapter: SpinnerAdapter<String>){
        this.spinnerAdapter = adapter
        notifyDataSetChanged()
    }

    fun filter(showFinished: Boolean = this.showFinished) {
        lessons = lessonsFull.filter { it.isFinished == showFinished }.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun clear() {
        this.lessons.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) return TYPE_SPINNER
        else TYPE_LESSON_OFFLINE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SPINNER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_school_offline_spinner, parent, false)
                SpinnerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson_offline_in_your_city, parent, false)
                OfflineLessonsViewHolder(view)
            }
        }

    }

    override fun getItemCount(): Int {
        return lessons.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is OfflineLessonsViewHolder -> {
                val lesson = lessons[position - 1]
                holder.bind(lesson)
            }
            is SpinnerViewHolder -> {
                holder.bind()
            }
        }
    }

    inner class OfflineLessonsViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val iVLesson = itemView.imageView_lesson
        private val iVStatus = itemView.imageViewStatus
        private val tVStatus = itemView.textView_lesson_status
        private val tVTitle = itemView.textViewLessonTitle
        private val tVDate = itemView.textView_lesson_date
        private val tVCity = itemView.textView_lesson_city

        fun bind(lesson:OfflineSeminars.OfflineLesson){
            Glide.with(itemView.context).load(baseUrl + lesson.imageUrl).placeholder(R.drawable.not_found).centerCrop()
                .into(iVLesson)
            if(lesson.isFinished) {
                tVStatus.text = "Завершено"
                iVStatus.background = itemView.resources.getDrawable(R.drawable.background_coupon_status_true)
            }
            if(!lesson.isFinished) {
                tVStatus.text = "Активно"
                iVStatus.background = itemView.resources.getDrawable(R.drawable.background_coupon_staus_false)
            }
            tVTitle.text = lesson.title
            tVDate.text = lesson.date
            tVCity.text = lesson.city
        }
    }

    inner class SpinnerViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        private val spinner: Spinner = itemView.spinner_cities
        fun bind(){
            spinnerAdapter?.run {
                spinner.adapter = spinnerAdapter
            }
        }
    }

    companion object {
        const val TYPE_LESSON_OFFLINE = 0
        const val TYPE_SPINNER = 1
    }

}