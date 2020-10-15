package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegInterestsBinding

class RegInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegInterestsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonFinish.setOnClickListener(onButtonFinishClicked)
    }

    private val onButtonFinishClicked: (View)->Unit = { _: View ->
        requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
    }

}