package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ItemSeminarBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.dateFormat
import ru.hvost.news.utils.getDefaultShimmer
import java.util.ArrayList

class SeminarsListAdapter(
        private val schoolVM: SchoolViewModel,
        private val clickSeminar: ((Long) -> Unit)? = null,
) : ListAdapter<OfflineSeminars.OfflineSeminar,
        RecyclerView.ViewHolder>(SeminarsContentDiffUtilCallback()) {

    private var lessonsFull = arrayListOf<OfflineSeminars.OfflineSeminar>()
    var lessons = arrayListOf<OfflineSeminars.OfflineSeminar>()
    private var showFinished = true


    fun filter(showFinished: Boolean = this.showFinished) {
        lessons = lessonsFull.filter { it.isFinished == showFinished }.toCollection(ArrayList())
        schoolVM.adapterSeminarsSize.value = lessons.size
        notifyDataSetChanged()
    }

    override fun submitList(list: List<OfflineSeminars.OfflineSeminar>?) {
        list?.let {
            this.lessonsFull = it.toCollection(ArrayList())
            this.lessons = it.toCollection(ArrayList())
        }
        schoolVM.adapterSeminarsSize.value = lessons.size
        super.submitList(lessons)
    }

    override fun onCurrentListChanged(
            previousList: MutableList<OfflineSeminars.OfflineSeminar>,
            currentList: MutableList<OfflineSeminars.OfflineSeminar>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        schoolVM.sendRecyclerSeminarsReadyEvent()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SeminarViewHolder(
                ItemSeminarBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                ))
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lesson = lessons[position]
        return (holder as SeminarViewHolder).bind(lesson)
    }

    class SeminarsContentDiffUtilCallback : DiffUtil.ItemCallback<OfflineSeminars.OfflineSeminar>() {

        override fun areItemsTheSame(oldItem: OfflineSeminars.OfflineSeminar, newItem: OfflineSeminars.OfflineSeminar): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OfflineSeminars.OfflineSeminar, newItem: OfflineSeminars.OfflineSeminar): Boolean {
            return oldItem == newItem
        }
    }

    inner class SeminarViewHolder(private val binding: ItemSeminarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(seminar: OfflineSeminars.OfflineSeminar) {
            Glide.with(itemView.context).load(APIService.baseUrl + seminar.imageUrl)
                    .placeholder(getDefaultShimmer(itemView.context))
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