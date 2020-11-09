package ru.hvost.news.presentation.adapters.spinners

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_spinner_item.view.*
import ru.hvost.news.R

class SpinnerCustomAdapter(ctx: Context,list:ArrayList<String>): ArrayAdapter<String>(ctx, 0,list ) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position:Int,convertView:View?, parent:ViewGroup?):View{

            Log.i("awd", "2")
            val convertView2 = LayoutInflater.from(context).inflate(R.layout.layout_spinner_item, parent, false)
            Log.i("awd", "3")
            val tVTitle = convertView2.textView9
            Log.i("awd", "4")
            val item = getItem(position)
            Log.i("awd", "5")
            item?.run {
                Log.i("awd", "6")
                tVTitle.text = this
            }

        Log.i("awd", "7")
        return convertView2!!
    }
}