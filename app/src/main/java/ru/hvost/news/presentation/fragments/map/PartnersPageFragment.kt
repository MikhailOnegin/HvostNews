package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentPartnersPageBinding
import ru.hvost.news.presentation.viewmodels.MapViewModel

class PartnersPageFragment:Fragment() {

    private lateinit var binding: FragmentPartnersPageBinding
    private lateinit var mapVM: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPartnersPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
    }
}