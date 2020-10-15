package ru.hvost.news.presentation.fragments.school.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentCourseInfoBinding
import ru.hvost.news.databinding.FragmentOnlineCourseActiveBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class CourseInfoFragment:Fragment() {

    private lateinit var binding: FragmentCourseInfoBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
    }
}