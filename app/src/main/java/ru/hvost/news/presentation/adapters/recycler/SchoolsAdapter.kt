package ru.hvost.news.presentation.adapters.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OnlineSchool

class SchoolsAdapter:RecyclerView.Adapter<SchoolsAdapter.ViewHolder>()  {

    private var schools = arrayListOf<OnlineSchool.School>()
    var clickSchool:ClickSchool? = null

    interface ClickSchool{
        fun onClick(school:OnlineSchool.School)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_online, parent, false)
        return ViewHolder(view)
    }

    fun setSchools(schools:List<OnlineSchool.School>){
        this.schools = schools.toCollection(ArrayList())
        Log.i("eeee","schools size: ${this.schools.size}")
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schools[position]
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val clSchool = itemView.constraint_school
        private val ivSchool = itemView.imageView_school
        private val tvTitle = itemView.textView_title
        private val tvRank = itemView.textView_rank
        fun bind(school:OnlineSchool.School){
            if(school.title.isNotBlank()) tvTitle.text = school.title
            if(school.userRank.isNotBlank())tvRank.text = school.userRank
            Glide.with(itemView.context).load(baseUrl + school.image).placeholder(R.drawable.not_found).centerCrop()
                .into(ivSchool)
            clSchool.setOnClickListener {
                clickSchool?.onClick(school)
            }
        }
    }
}