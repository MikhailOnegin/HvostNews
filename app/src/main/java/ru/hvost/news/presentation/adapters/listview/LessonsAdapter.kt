package ru.hvost.news.presentation.adapters.listview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_school_online.view.*
import ru.hvost.news.models.OnlineLessons

class LessonsAdapter(var ctx:Context, var resources:Int,
var items:List<OnlineLessons.OnlineLesson>
): ArrayAdapter<OnlineLessons.OnlineLesson>(ctx, resources, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resources, null)
        val tVTitle = view.textView_title
        var lesson = items[position]
        tVTitle.text = lesson.lessonTitle
        return super.getView(position, convertView, parent)

    }
}