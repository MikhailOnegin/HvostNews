package ru.hvost.news.presentation.adapters.recycler


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.OnlineSchools

class SchoolsOnlineAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var schools = arrayListOf<OnlineSchools.School>()
    var clickSchool: ClickSchool? = null

    interface ClickSchool {
        fun onClick(schools: OnlineSchools.School)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_SCHOOL_ONLINE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_school_online, parent, false)
                SchoolsViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_school_online_spinner, parent, false)
                SpinnerViewHolder(view)
            }
        }
    }

    fun setSchools(schools: List<OnlineSchools.School>) {
        this.schools = schools.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0 ) return TYPE_SPINNER
        else TYPE_SCHOOL_ONLINE
    }

    override fun getItemCount(): Int {
        return schools.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is SchoolsViewHolder -> {
                val item = schools[position - 1]
                holder.bind(item)
            }
            is SpinnerViewHolder -> {
                holder.bind()
            }
        }
    }

    inner class SchoolsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clSchool = itemView.constraint_school
        private val ivSchool = itemView.imageView_school
        private val tvTitle = itemView.textView_title
        private val tvRank = itemView.textView_rank

        fun bind(schools: OnlineSchools.School) {
            if (schools.title.isNotBlank()) tvTitle.text = schools.title
            if (schools.userRank.isNotBlank()) tvRank.text = schools.userRank

            Glide.with(itemView.context).load(baseUrl + schools.image)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(ivSchool)

            clSchool.setOnClickListener {
                clickSchool?.onClick(schools)
            }
        }
    }

    inner class SpinnerViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        fun bind(){

        }
    }

    companion object{
        const val TYPE_SCHOOL_ONLINE = 0
        const val TYPE_SPINNER = 1
    }
}