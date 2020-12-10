package ru.hvost.news.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentEditProfileBinding
import ru.hvost.news.databinding.FragmentPetPassportBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class PetPassportFragment : BaseFragment() {

    private lateinit var binding: FragmentPetPassportBinding
    private lateinit var mainVM: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetPassportBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

}