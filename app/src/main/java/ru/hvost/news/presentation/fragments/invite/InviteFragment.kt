package ru.hvost.news.presentation.fragments.invite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentEditProfileBinding
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.utils.enums.State
import java.util.*

class InviteFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainVM.getBonusBalance()
        setObservers()
    }

    private fun setObservers() {
        mainVM.bonusBalanceState.observe(viewLifecycleOwner, { onBalanceChanged(it) })
    }

    private fun onBalanceChanged(state: State?) {
        when (state) {
            State.SUCCESS -> { setBalance() }
            State.FAILURE, State.ERROR -> { }
        }
    }

    private fun setBalance() {
        binding.balance.text = mainVM.bonusBalanceResponse.value?.balance.toString()
    }
}