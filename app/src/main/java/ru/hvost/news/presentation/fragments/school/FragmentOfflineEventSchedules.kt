package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.databinding.FragmentSchoolOfflineEventSchedulesBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class FragmentOfflineEventSchedules: BaseFragment() {


    private lateinit var binding: FragmentSchoolOfflineEventSchedulesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOfflineEventSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}