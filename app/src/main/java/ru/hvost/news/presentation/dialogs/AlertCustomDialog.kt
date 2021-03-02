package ru.hvost.news.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogAlertBinding

class AlertCustomDialog(
    private val message: String,
    private val onSubmitButtonClicked: (() -> Unit)?,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAlertBinding.inflate(inflater, container, false)
        binding.message.text = message
        binding.buttonSubmit.setOnClickListener {
            onSubmitButtonClicked?.invoke()
            dismiss()
        }
        return binding.root
    }

}