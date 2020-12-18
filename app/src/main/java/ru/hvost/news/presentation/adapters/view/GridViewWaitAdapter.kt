package ru.hvost.news.presentation.adapters.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_what_wait.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.OnlineSchools

class GridViewWaitAdapter(private val context:Context, private val wait:List<OnlineSchools.Wait>): BaseAdapter(){
    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = View.inflate(context, R.layout.layout_what_wait, null)
        val iVLogo = view.imageView
        val tVSection = view.textView_section
        val tVDescription = view.textView_description
        val waitItem = wait[p0]
        Glide.with(context).load(APIService.baseUrl + waitItem.imageUrl)
            .placeholder(R.drawable.not_found).centerCrop()
            .into(iVLogo)
        tVSection.text = waitItem.head
        return view
    }

    override fun getItem(p0: Int): Any {
       return wait[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return wait.size
    }
}