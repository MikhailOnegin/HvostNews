package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.textView_title
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ItemSchoolLessonOnlineActiveBinding
import ru.hvost.news.databinding.ItemSchoolLessonOnlineFinishedBinding
import ru.hvost.news.databinding.ItemUsefulLiteratureBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.utils.startIntentActionView
import java.lang.StringBuilder

class SchoolOnlineMaterialsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var school:OnlineSchools.OnlineSchool? = null
    private var onlineLessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var onClickLessonActive: OnClickLessonActive? = null
    var onClickLessonFinished: OnClickLessonFinished? = null
    private var firstActiveLessonId:String? = null

    interface OnClickLessonActive {
        fun onClick(lessonId:String)
    }
    interface OnClickLessonFinished {
        fun onClick(lessonId:String)
    }


    fun setLessons(lessons: List<OnlineLessons.OnlineLesson>) {
        for(i in lessons.indices){
            val firstActiveLesson = lessons[i]
            if(!firstActiveLesson.isTestPassed){
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
        return when (viewType) {
            TYPE_LESSON_ACTIVE -> LessonActiveViewHolder(ItemSchoolLessonOnlineActiveBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ))
            TYPE_LESSON_FINISHED -> LessonFinishedViewHolder(ItemSchoolLessonOnlineFinishedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ))
            else -> UsefulLiteratureViewHolder(ItemUsefulLiteratureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= onlineLessons.size) TYPE_USEFUL_LITERATURE
        else {
            if(!onlineLessons[position].isTestPassed){
                TYPE_LESSON_ACTIVE
            } else {
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
                holder.bind(lesson, position + 1)
            }
            is LessonFinishedViewHolder -> {
                val lesson = onlineLessons[position]
                holder.bind(lesson, position + 1)
            }
            is UsefulLiteratureViewHolder -> holder.bind(school)
        }
    }


    inner class LessonActiveViewHolder(private val binding:ItemSchoolLessonOnlineActiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: OnlineLessons.OnlineLesson, lessonNumber: Int) {
            binding.textViewNumber.text = lessonNumber.toString()
            binding.textViewTitle.text = lesson.lessonTitle.parseAsHtml()
            val strBuilder = StringBuilder()
            strBuilder.append(itemView.resources.getString(R.string.age2))
            for(i in lesson.petAge.indices){
                val age = lesson.petAge[i]
                if(i==0) strBuilder.append(age)
                if(i==1) strBuilder.append("-$age")
            }
                binding.textViewAge.text = strBuilder.toString()
            firstActiveLessonId?.run {
                if (this == lesson.lessonId) {
                    binding.imageViewPlay.visibility = View.VISIBLE
                    binding.textViewTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray1))
                    binding.constraintLesson.setOnClickListener {
                        onClickLessonActive?.onClick(lesson.lessonId)
                    }
                }
                else {
                    binding.imageViewPlay.visibility = View.GONE
                    binding.textViewTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray3))
                }
            }
        }
    }

    inner class LessonFinishedViewHolder(private val binding: ItemSchoolLessonOnlineFinishedBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(lesson: OnlineLessons.OnlineLesson, lessonNumber: Int){
            binding.textViewNumberFinished.text = lessonNumber.toString()
            binding.textViewTitleFinished.text = lesson.lessonTitle.parseAsHtml()
            binding.constraintLessonFinished.setOnClickListener {
                onClickLessonFinished?.onClick(lesson.lessonId)
            }
        }
    }

    inner class UsefulLiteratureViewHolder(private val binding:ItemUsefulLiteratureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(school: OnlineSchools.OnlineSchool?) {
            school?.run {
                if (school.literature.isNotEmpty()) {

                    val container = itemView.linearLayout_literature
                    container.removeAllViews()
                    for (i in school.literature.indices) {
                        val literature = school.literature[i]
                        val viewLiterature = LayoutLiteratureItemBinding.inflate(
                            LayoutInflater.from(itemView.context),
                            container,
                            false
                        ).root

                        viewLiterature.textView_title.text = school.literature[i].title
                        viewLiterature.textView_pet.text = school.literature[i].pet
                        viewLiterature.constraint_literature.setOnClickListener {
                            startIntentActionView(itemView.context, APIService.baseUrl + literature.fileUrl)
                        }
                        val margin = itemView.resources.getDimension(R.dimen.largeMargin).toInt()

                        if(i == school.literature.lastIndex) {
                            (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                                margin,
                                0,
                                margin,
                                0
                            )
                        }
                        else {
                            (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                                margin,
                                0,
                                0,
                                0
                            )
                        }
                        container.addView(viewLiterature)
                    }
                }
                else {
                   binding.rootConstraint.visibility = View.GONE
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