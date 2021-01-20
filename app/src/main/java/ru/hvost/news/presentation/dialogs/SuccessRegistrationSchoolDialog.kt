package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogSchoolRegistrationSuccessBinding

class SuccessRegistrationSchoolDialog(private val title:String ): BottomSheetDialogFragment() {

    private lateinit var binding: DialogSchoolRegistrationSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSchoolRegistrationSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val description = "Регистрация прошла успешно, вы зарегистрированы на этот семинар " +
                "\"$title\""
        binding.textViewDescription.text = description
        setListeners()
    }

    private fun setListeners() {
        binding.buttonOk.setOnClickListener {
            this.dismiss()
        }
    }
}