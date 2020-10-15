package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegUserBinding

class RegUserFragment : Fragment() {

    private lateinit var binding: FragmentRegUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener(onButtonNextClicked)
    }

    private val onButtonNextClicked = { _: View ->
        findNavController().navigate(R.id.action_regUserFragment_to_regPetFragment)
    }

}