package ru.hvost.news.presentation.adapters.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_lesson_number.view.*
import kotlinx.android.synthetic.main.layout_lesson_numbers.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ItemSchoolOnlineBinding
import ru.hvost.news.databinding.LayoutLessonNumberBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getDefaultShimmer

class SchoolsListAdapter(
    private val schoolVM: SchoolViewModel,
    private val clickSchool: ((Long) -> Unit)? = null,
): ListAdapter<OnlineSchools.OnlineSchool, RecyclerView.ViewHolder>(SchoolsContentDiffUtilCallback()) {

    private var schools = arrayListOf<OnlineSchools.OnlineSchool>()

    fun filterYourSchools(s:String, context: Context){
        when(s){
            context.getString(R.string.your_seminars) -> schools = currentList.filter { it.participate }.toCollection(ArrayList())
            context.getString(R.string.all_seminars) -> schools = currentList.toCollection(ArrayList())
        }
        schoolVM.adapterSchoolsSize.value = schools.size
        notifyDataSetChanged()
    }

    override fun submitList(list: List<OnlineSchools.OnlineSchool>?) {
        list?.let {
            schools = list.toCollection(ArrayList())
            schoolVM.adapterSchoolsSize.value = schools.size
        }
        super.submitList(list)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<OnlineSchools.OnlineSchool>,
        currentList: MutableList<OnlineSchools.OnlineSchool>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        schoolVM.sendRecyclerSchoolsReadyEvent()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SchoolsViewHolder(
            ItemSchoolOnlineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val school = schools[position]
        (holder as SchoolsViewHolder).bind(school)
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    class SchoolsContentDiffUtilCallback : DiffUtil.ItemCallback<OnlineSchools.OnlineSchool>() {

        override fun areItemsTheSame(oldItem: OnlineSchools.OnlineSchool, newItem: OnlineSchools.OnlineSchool): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OnlineSchools.OnlineSchool, newItem: OnlineSchools.OnlineSchool): Boolean {
            return oldItem == newItem
        }
    }


    inner class SchoolsViewHolder(private val binding: ItemSchoolOnlineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(school: OnlineSchools.OnlineSchool) {
            if (school.title.isNotBlank()) binding.textViewTitle.text = school.title.parseAsHtml()
            if (school.participate) {
                binding.constraintRank.visibility = View.VISIBLE
                binding.textViewRank.text = school.userRank
            } else {
                binding.constraintRank.visibility = View.INVISIBLE
            }
            if (school.isNew) binding.constraintNew.visibility = View.VISIBLE
            else binding.constraintNew.visibility = View.GONE
            Glide.with(itemView.context)
                    .load(APIService.baseUrl + school.image)
                    .placeholder(getDefaultShimmer(itemView.context))
                    .error(R.drawable.empty_image)
                    .into(binding.imageViewSchool)
            binding.rootConstraint.setOnClickListener {
                clickSchool?.invoke(school.id)
            }

            val containerNumbers = itemView.linearLayout_lesson_numbers
            containerNumbers.removeAllViews()
            for(i in school.lessonsPassed.indices){
                val number = (i + 1).toString()
                val isPassed = school.lessonsPassed[i].isPassed
                val viewWait = LayoutLessonNumberBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    containerNumbers,
                    false
                ).root

                viewWait.textView_lesson_number.text = number
                viewWait.textView_lesson_number.isSelected = isPassed
                if (isPassed) viewWait.textView_lesson_number.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        android.R.color.white
                    )
                )
                else viewWait.textView_lesson_number.setTextColor(
                        ContextCompat.getColor(
                                itemView.context,
                                R.color.gray3
                        )
                )

                val paddingNormal = itemView.resources.getDimension(R.dimen._14dp).toInt()
                val paddingEdge = itemView.resources.getDimension(R.dimen.largeMargin).toInt()
                if (i == 0 || i == school.lessonsPassed.lastIndex) {
                    if (i == 0) {
                        viewWait.setPadding(paddingEdge, 0, paddingNormal, 0)
                    }
                    if (i == school.lessonsPassed.lastIndex) {
                        viewWait.setPadding(0, 0, paddingEdge, 0)
                    }
                } else {
                    viewWait.setPadding(0, 0, paddingNormal, 0)
                }
                containerNumbers.addView(viewWait)
            }
        }
    }

}