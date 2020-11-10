package ru.hvost.news.presentation.fragments.school.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOnlineBinding
import ru.hvost.news.models.OnlineSchool
import ru.hvost.news.presentation.adapters.viewPager.OnlineSchoolActiveVPAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineCourseActiveFragment:Fragment() {

    private lateinit var binding: FragmentSchoolOnlineBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOnlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        navC = findNavController()

        val school = arguments?.get("school")
        if(school is OnlineSchool.School){
            if(school.title.isNotBlank()) binding.textViewTitle.text = school.title
            if(school.userRank.isNotBlank()) binding.textViewRank.text = school.userRank
        }
        setListeners()
        initViewPager()
    }

    fun setListeners() {
       binding.imageButtonBack.setOnClickListener {
           navC.popBackStack()
       }
     //   binding.viewPager.adapter = Paren
//
        //binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
        //    override fun onTabReselected(tab: TabLayout.Tab?) {
        //    }
//
        //    override fun onTabUnselected(tab: TabLayout.Tab?) {
        //    }
//
        //override fun onTabSelected(tab: TabLayout.Tab?) {
        //    if (tab?.position == 0){
        //        binding.scrollView.noti
        //        binding.includeMaterials.root.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        //        binding.includeInfo.root.layoutParams = ConstraintLayout.LayoutParams(0, 0)
        //    }
        //    if (tab?.position == 1){
        //        binding.includeMaterials.root.layoutParams = ConstraintLayout.LayoutParams(0,0)
        //        binding.includeInfo.root.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        //    }
        //}
    }
    private fun initViewPager(){
        val adapter = OnlineSchoolActiveVPAdapter(requireActivity().supportFragmentManager, this.lifecycle)
        binding.viewPager.adapter = adapter
        val names:Array<String> = arrayOf(getString(R.string.about_lessons), getString(R.string.materials_of_lessons))
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            tab.text = names[position]
        }.attach()
    }
}