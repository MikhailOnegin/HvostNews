package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
        binding.root.setOnClickListener {
            /* Пустой листенер позволяет перехватывать событие,
            чтобы не скроллился контент на заднем фоне */
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setListeners()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setListeners() {
        binding.next.setOnClickListener { findNavController().navigate(R.id.action_regPopup_to_invitePopup) }
    }
}