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
import ru.hvost.news.databinding.FragmentSchoolOnlineBinding
import ru.hvost.news.models.OnlineSchool
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
    }

    fun setListeners(){
        binding.imageButtonBack.setOnClickListener {
            navC.popBackStack()
        }
    }

}