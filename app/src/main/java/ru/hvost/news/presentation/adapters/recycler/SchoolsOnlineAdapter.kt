package ru.hvost.news.presentation.adapters.recycler


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools

class SchoolsOnlineAdapter : RecyclerView.Adapter<SchoolsOnlineAdapter.SchoolsViewHolder>() {

    private var schools = arrayListOf<OnlineSchools.OnlineSchool>()
    private var lessons = arrayListOf<OnlineLessons.OnlineLesson>()
    var clickSchool: ClickSchool? = null

    interface ClickSchool {
        fun onClick(schoolId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_school_online, parent, false)
        return SchoolsViewHolder(view)
    }


    fun setSchools(onlineSchools: List<OnlineSchools.OnlineSchool>) {
        this.schools = onlineSchools.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    override fun onBindViewHolder(holder: SchoolsViewHolder, position: Int) {
        val item = schools[position]
        holder.bind(item)
    }


    inner class SchoolsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clSchool = itemView.constraint_school
        private val ivSchool = itemView.imageView_school
        private val tvTitle = itemView.textView_title
        private val tvRank = itemView.textView_rank
        private val tVButton1 = itemView.button1
        private val tVButton2 = itemView.button2
        private val tVButton3 = itemView.button3
        private val tVButton4 = itemView.button4
        private val tVButton5 = itemView.button5
        private val tVButton6 = itemView.button6
        private val tVButton7 = itemView.button7
        private val tVButton8 = itemView.button


        fun bind(onlineSchool: OnlineSchools.OnlineSchool) {
            if (onlineSchool.title.isNotBlank()) tvTitle.text = onlineSchool.title
            if (onlineSchool.userRank.isNotBlank()) tvRank.text = onlineSchool.userRank
            Glide.with(itemView.context).load(baseUrl + onlineSchool.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(ivSchool)
            clSchool.setOnClickListener {
                clickSchool?.onClick(onlineSchool.id.toString())
            }
            for(i in onlineSchool.lessonsPassed.indices){
                val number = onlineSchool.lessonsPassed[i].number
                val isPassed = onlineSchool.lessonsPassed[i].isPassed
                when(number){
                    1 -> if (isPassed) {
                        tVButton1.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton1.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton1.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton1.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }

                    2 -> if (isPassed) {
                        tVButton2.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton2.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton2.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton2.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    3 -> if (isPassed) {
                        tVButton3.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton3.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton3.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton3.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    4 -> if (isPassed) {
                        tVButton4.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton4.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton4.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton4.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    5 -> if (isPassed) {
                        tVButton5.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton5.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton5.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton5.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    6 -> if (isPassed) {
                        tVButton6.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton6.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton6.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton6.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    7 -> if (isPassed) {
                        tVButton7.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton7.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton7.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton7.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                    8 -> if (isPassed) {
                        tVButton8.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.colorPrimary))
                        tVButton8.setTextColor(itemView.resources.getColor(android.R.color.white)) }
                    else{
                        tVButton8.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.gray5))
                        tVButton8.setTextColor(itemView.resources.getColor(R.color.TextColorPrimary)) }
                }
            }
        }
    }
}