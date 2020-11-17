package ru.hvost.news.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_qr_code.view.*
import ru.hvost.news.R

class QrCodeDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_qr_code, container, false)
        rootView.linearLayout.setOnClickListener {
            dismiss()
        }
        return rootView
    }
}