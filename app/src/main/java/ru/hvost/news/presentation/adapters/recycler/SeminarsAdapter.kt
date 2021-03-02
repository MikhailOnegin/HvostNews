package ru.hvost.news.presentation.adapters.recycler


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.ItemSeminarBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.dateFormat
import java.util.*

class SeminarsAdapter(
    private val schoolVM: SchoolViewModel,
    private val clickSeminar: ((Long) -> Unit)? = null,
) :
    RecyclerView.Adapter<SeminarsAdapter.OfflineLessonsViewHolder>() {

    private var lessonsFull = arrayListOf<OfflineSeminars.OfflineSeminar>()
    var lessons = arrayListOf<OfflineSeminars.OfflineSeminar>()
    private var showFinished = true



    fun filter(showFinished: Boolean = this.showFinished) {
        lessons = lessonsFull.filter { it.isFinished == showFinished }.toCollection(ArrayList())
        schoolVM.adapterSeminarsSize.value = lessons.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineLessonsViewHolder {
        return OfflineLessonsViewHolder(
            ItemSeminarBinding.inflate(
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

    inner class OfflineLessonsViewHolder(private val binding:ItemSeminarBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(seminar: OfflineSeminars.OfflineSeminar) {
            Glide.with(itemView.context).load(baseUrl + seminar.imageUrl)
                .placeholder(R.drawable.loader_anim)
                .error(R.drawable.ic_load_error)
                .centerCrop()
                .into(binding.imageViewLesson)
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
                clickSeminar?.invoke(seminar.id)
            }
        }
    }
}
