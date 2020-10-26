package ru.hvost.news.presentation.fragments.school.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentOnlineCourseLessonBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineCourseLessonFragment:Fragment() {

    private lateinit var binding: FragmentOnlineCourseLessonBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineCourseLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
    }
}