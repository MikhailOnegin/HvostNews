package ru.hvost.news.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogSubmitActionBinding

class SubmitActionDialog(
    private val title: String,
    private val message: String,
    private val onSubmitButtonClicked: (() -> Unit),
    private val onCancelButtonClicked: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSubmitActionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSubmitActionBinding.inflate(inflater, container, false)
        binding.title.text = title
        binding.message.text = message
        binding.buttonSubmit.setOnClickListener {
            onSubmitButtonClicked.invoke()
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            onCancelButtonClicked?.invoke()
            dismiss()
        }
        return binding.root
    }

}