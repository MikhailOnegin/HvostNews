package ru.hvost.news.presentation.fragments.school.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import ru.hvost.news.databinding.FragmentOnlineCourseActiveBinding
import ru.hvost.news.presentation.adapters.viewPager.OnlineSchoolActiveVPAdapter
import ru.hvost.news.presentation.adapters.viewPager.ParentsSchoolVPAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineCourseActiveFragment:Fragment() {

    private lateinit var binding: FragmentOnlineCourseActiveBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineCourseActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        initViewPager()
    }
    private fun initViewPager(){
        val adapter = OnlineSchoolActiveVPAdapter(requireActivity().supportFragmentManager, this.lifecycle)
        binding.viewPager.adapter = adapter
        val names:Array<String> = arrayOf("О курсе", "Материалы курса")
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            tab.text = names[position]
        }.attach()
    }

}