package ru.hvost.news.presentation.adapters.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.models.OnlineSchool

class OnlineSchoolsAdapter: RecyclerView.Adapter<OnlineSchoolsAdapter.SchoolViewHolder>() {

    private var schools = arrayListOf<OnlineSchool.School>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        // when(viewType){
        //     TYPE_SCHOOL ->
        //   }
        TODO("Not yet implemented")
    }

    fun setSchools(schools:List<OnlineSchool.School>){
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class SchoolViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){

    }

    companion object {
        const val TYPE_SCHOOL = 1
        const val TYPE_USEFUL_LITERATURE = 2
        const val TYPE_USEFUL_LITERATURE_RECYCLER = 3
    }
}