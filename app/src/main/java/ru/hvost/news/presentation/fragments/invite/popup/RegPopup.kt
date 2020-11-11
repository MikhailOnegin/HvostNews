package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.databinding.FragmentRegPopupBinding

class RegPopup : Fragment() {

    private lateinit var binding: FragmentRegPopupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegPopupBinding.inflate(inflater, container, false)
        return binding.root
    }
}