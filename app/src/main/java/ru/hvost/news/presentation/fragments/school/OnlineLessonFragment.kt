package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineLessonFragment : Fragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonBinding
    private lateinit var schoolVM:SchoolViewModel
    private var lessonId:Any? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOnlineLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[ SchoolViewModel::class.java]
        lessonId = arguments?.get("lessonId")
        setSystemUiVisibility()
        setObservers(this)
    }

    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.onlineLessons.observe(owner, Observer {
            lessonId?.run {
                for (i in it.lessons.indices){
                    val lesson = it.lessons[i]
                    if(lesson.domainId == this){
                        binding.textViewTitle.text = lesson.lessonTitle
                        binding.textViewLessonNumber.text = lesson.lessonNumber.toString()
                        binding.textViewQuestion.text = lesson.testQuestion
                        return@Observer
                    }
                }
            }
        })

    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }




}