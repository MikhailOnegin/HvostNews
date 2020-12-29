package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.databinding.FragmentSchoolOfflineEventInfoBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class FragmentOfflineEventInfo: BaseFragment() {

    private lateinit var binding:FragmentSchoolOfflineEventInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOfflineEventInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}