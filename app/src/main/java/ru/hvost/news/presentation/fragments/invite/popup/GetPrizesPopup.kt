package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentGetPrizesPopupBinding

class GetPrizesPopup : Fragment() {

    private lateinit var binding: FragmentGetPrizesPopupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetPrizesPopupBinding.inflate(inflater, container, false)
        return binding.root
    }
}