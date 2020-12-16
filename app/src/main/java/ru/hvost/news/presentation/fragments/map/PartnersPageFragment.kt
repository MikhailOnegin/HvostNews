package ru.hvost.news.presentation.fragments.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentMapPartnersPageBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class PartnersPageFragment :    BaseFragment() {

    private lateinit var binding: FragmentMapPartnersPageBinding
    private lateinit var mapVM: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapPartnersPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
    }

}