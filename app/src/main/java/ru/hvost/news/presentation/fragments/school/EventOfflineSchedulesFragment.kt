package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.databinding.FragmentEventOfflineSchedulesBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class EventOfflineSchedulesFragment: BaseFragment() {


    private lateinit var binding: FragmentEventOfflineSchedulesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventOfflineSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}