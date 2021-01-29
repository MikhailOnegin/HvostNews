package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.databinding.FragmentInvitePopupBinding

class InvitePopup : Fragment() {

    private lateinit var binding: FragmentInvitePopupBinding
    private val navOptions = NavOptions.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvitePopupBinding.inflate(inflater, container, false)
        initializeNavOptions()
        return binding.root
    }

    private fun initializeNavOptions() {
        navOptions
            .setEnterAnim(R.anim.fragment_enter)
            .setExitAnim(R.anim.fragment_exit)
            .setPopEnterAnim(R.anim.fragment_enter)
            .setPopExitAnim(R.anim.fragment_exit)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setListeners()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setListeners() {
        binding.root.setOnClickListener { }
        binding.next.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_invite_instructions)
                .navigate(R.id.action_invitePopup_to_getPrizesPopup, null, navOptions.build())
        }
    }
}