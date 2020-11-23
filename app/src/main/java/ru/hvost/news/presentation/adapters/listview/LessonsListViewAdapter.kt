package ru.hvost.news.presentation.adapters.listview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_lesson_online.view.*
import kotlinx.android.synthetic.main.item_school_online.view.textView_title
import ru.hvost.news.R
import ru.hvost.news.models.OnlineLessons

class LessonsListViewAdapter(private val ctx:Context, var resources:Int,
                             private val items:List<OnlineLessons.OnlineLesson>,
                             private val listenerGo: (OnlineLessons.OnlineLesson) -> Unit
): ArrayAdapter<OnlineLessons.OnlineLesson>(ctx, resources, items) {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resources, null)
        val lesson = items[position]

        val tVTitle = view.textView_title
        val tVNumber = view.textView_number
        val tVAge = view.textView_age
        val iVGo = view.imageView_go

        val age = "${ctx.resources.getString(R.string.age2)} ${lesson.petAge}"
        tVTitle.text = lesson.lessonTitle
        tVNumber.text = lesson.lessonNumber.toString()
        tVAge.text = age
        iVGo.setOnClickListener {
            listenerGo.invoke(lesson)
        }
        return view
    }
}