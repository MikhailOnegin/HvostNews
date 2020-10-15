package ru.hvost.news.presentation.fragments.school.parents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.presentation.adapters.viewPager.ParentsSchoolVPAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class ParentsSchoolFragment:Fragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        initViewPager()
    }

    private fun initViewPager(){
        val adapter = ParentsSchoolVPAdapter(requireActivity().supportFragmentManager, this.lifecycle)
        binding.viewPager.adapter = adapter
        val names:Array<String> = arrayOf("Онлайн школа", "Семинары в вашем городе")
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            tab.text = names[position]
        }.attach()
    }

}