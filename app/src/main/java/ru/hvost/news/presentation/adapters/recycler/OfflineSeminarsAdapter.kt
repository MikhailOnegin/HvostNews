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
import ru.hvost.news.databinding.ItemSchoolOfflineSeminarBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.utils.dateFormat
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
        fun onClick(lessonId:Int)
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
        return OfflineLessonsViewHolder(ItemSchoolOfflineSeminarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }


    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: OfflineLessonsViewHolder, position: Int) {
        val lesson = lessons[position]
        return holder.bind(lesson)
    }

    inner class OfflineLessonsViewHolder(private val binding:ItemSchoolOfflineSeminarBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(seminar: OfflineSeminars.OfflineSeminar) {
            Glide.with(itemView.context).load(baseUrl + seminar.imageUrl)
                .placeholder(R.drawable.not_found).centerCrop().into(binding.imageViewLesson)
            if (seminar.isFinished) {
                binding.textViewLessonStatus.text = "Завершено"
                binding.imageViewStatus.isSelected = false
            }
            if (!seminar.isFinished) {
                binding.textViewLessonStatus.text = "Активно"
                binding.imageViewStatus.isSelected = true
            }
            binding.textViewLessonTitle.text = seminar.title.parseAsHtml()
            binding.textViewLessonDate.text = dateFormat(seminar.date)
            binding.textViewLessonCity.text = seminar.city

            binding.rootConstraint.setOnClickListener {
                onClickLesson?.onClick(seminar.id)
            }
        }


    }
}
