package ru.hvost.news.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_map_filter.view.*
import kotlinx.android.synthetic.main.dialog_qr_code.view.*
import ru.hvost.news.R

class MapFilterDialog:DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_map_filter, container, false)
        rootView.button_show.setOnClickListener {
            dismiss()
        }
        return rootView
    }
}