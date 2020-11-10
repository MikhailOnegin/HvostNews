package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.databinding.FragmentInvitePopupBinding

class InvitePopup : Fragment() {

    private lateinit var binding: FragmentInvitePopupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvitePopupBinding.inflate(inflater, container, false)
        return binding.root
    }
}