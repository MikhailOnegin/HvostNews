package ru.hvost.news.presentation.adapters.recycler


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_lesson_number.view.*
import kotlinx.android.synthetic.main.layout_lesson_numbers.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.ItemSchoolOnlineBinding
import ru.hvost.news.databinding.LayoutLessonNumberBinding
import ru.hvost.news.models.OnlineSchools

class SchoolsAdapter(
        private val clickSchool: ((Long) -> Unit)? = null,
) : RecyclerView.Adapter<SchoolsAdapter.SchoolsViewHolder>() {
    private var schoolsFull = arrayListOf<OnlineSchools.OnlineSchool>()
    private var schools = arrayListOf<OnlineSchools.OnlineSchool>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolsViewHolder {
        return SchoolsViewHolder(ItemSchoolOnlineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }


    fun setSchools(onlineSchools: List<OnlineSchools.OnlineSchool>) {
        this.schoolsFull = onlineSchools.toCollection(ArrayList())
        this.schools = onlineSchools.toCollection(ArrayList())
        notifyDataSetChanged()
    }
    fun filterYourSchools(s:String){
        when(s){
            "Ваши семинары" -> schools = schoolsFull.filter { it.participate }.toCollection(ArrayList())
            "Все семинары" -> schools = schoolsFull
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    override fun onBindViewHolder(holder: SchoolsViewHolder, position: Int) {
        val item = schools[position]
        holder.bind(item)
    }


    inner class SchoolsViewHolder(private val binding:ItemSchoolOnlineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onlineSchool: OnlineSchools.OnlineSchool) {
            if (onlineSchool.title.isNotBlank()) binding.textViewTitle.text = onlineSchool.title.parseAsHtml()
            if (onlineSchool.userRank.isNotBlank()) binding.textViewRank.text = onlineSchool.userRank
            if(onlineSchool.isNew) binding.constraintNew.visibility = View.VISIBLE
            else binding.constraintNew.visibility = View.GONE
            Glide.with(itemView.context).load(baseUrl + onlineSchool.image)
                .placeholder(R.drawable.empty_image).centerCrop()
                .into(binding.imageViewSchool)
            binding.rootConstraint.setOnClickListener {
                clickSchool?.invoke(onlineSchool.id)
            }

            val containerNumbers = itemView.linearLayout_lesson_numbers
            containerNumbers.removeAllViews()
            for(i in onlineSchool.lessonsPassed.indices){
                val number = (i + 1).toString()
                val isPassed = onlineSchool.lessonsPassed[i].isPassed
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
                if (i == 0 || i == onlineSchool.lessonsPassed.lastIndex) {
                    if (i == 0) {
                        viewWait.setPadding(paddingEdge, 0, paddingNormal, 0)
                    }
                    if (i == onlineSchool.lessonsPassed.lastIndex) {
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