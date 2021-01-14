package ru.hvost.news.presentation.adapters.recycler


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_offline_seminar.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OfflineSeminars
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class OfflineSeminarsAdapter :
    RecyclerView.Adapter<OfflineSeminarsAdapter.OfflineLessonsViewHolder>() {

    private var lessonsFull = arrayListOf<OfflineSeminars.OfflineSeminar>()
    private var lessons = arrayListOf<OfflineSeminars.OfflineSeminar>()
    private var showFinished = true
    var onClickLesson:OnClickOfflineLesson? = null

    interface OnClickOfflineLesson{
        fun onClick(lessonId:String)
    }

    fun setSeminars(seminars: List<OfflineSeminars.OfflineSeminar>) {
        this.lessonsFull = seminars.toCollection(ArrayList())
        this.lessons = seminars.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun filter(showFinished: Boolean = this.showFinished) {
        lessons = lessonsFull.filter { it.isFinished == showFinished }.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineLessonsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_offline_seminar, parent, false)
        return OfflineLessonsViewHolder(view)
    }


    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: OfflineLessonsViewHolder, position: Int) {
        val lesson = lessons[position]
        return holder.bind(lesson)
    }

    inner class OfflineLessonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val constraint = itemView.constraint
        private val iVLesson = itemView.imageView_lesson
        private val iVStatus = itemView.imageViewStatus
        private val tVStatus = itemView.textView_lesson_status
        private val tVTitle = itemView.textViewLessonTitle
        private val tVDate = itemView.textView_lesson_date
        private val tVCity = itemView.textView_lesson_city

        fun bind(seminar: OfflineSeminars.OfflineSeminar) {
            Glide.with(itemView.context).load(baseUrl + seminar.imageUrl)
                .placeholder(R.drawable.not_found).centerCrop().into(iVLesson)
            if (seminar.isFinished) {
                tVStatus.text = "Завершено"
                iVStatus.isSelected = false
            }
            if (!seminar.isFinished) {
                tVStatus.text = "Активно"
                iVStatus.isSelected = true
            }
            tVTitle.text = seminar.title.parseAsHtml()
            tVDate.text = dateFormat(seminar.date)
            tVCity.text = seminar.city

            constraint.setOnClickListener {
                onClickLesson?.onClick(seminar.id.toString())
            }
        }

        private fun dateFormat(s:String): String {
            var result = ""
            try {
                val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale("ru", "RU"))
                val jsonDate = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
                val date  = jsonDate.parse(s)
                date?.run {
                    result = dateFormat.format(this)
                }
            }
            catch (e:Exception){
            }
            return result
        }
    }
}
