package ru.hvost.news.presentation.adapters.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school.view.*
import ru.hvost.news.R
import ru.hvost.news.models.OnlineSchool

class SchoolsAdapter:RecyclerView.Adapter<SchoolsAdapter.ViewHolder>()  {

    private var schools = arrayListOf<OnlineSchool.School>()
    var clickSchool:ClickSchool? = null

    interface ClickSchool{
        fun onClick(school:OnlineSchool.School)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school, parent, false)
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
        fun bind(item:OnlineSchool.School){
            tvTitle.text = item.title
            tvRank.text = item.userRank
            Glide.with(itemView.context).load(item.image).placeholder(R.drawable.not_found).centerCrop()
                .into(ivSchool)
            clSchool.setOnClickListener {
                clickSchool?.onClick(item)
            }
        }
    }
}