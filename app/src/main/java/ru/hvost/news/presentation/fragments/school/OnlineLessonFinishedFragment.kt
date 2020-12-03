package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_online_lesson_option.view.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonFinishedBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.databinding.LayoutOnlineLessonOptionBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineLessonFinishedFragment: Fragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonFinishedBinding
    private lateinit var schoolVM: SchoolViewModel
    private var lessonId:Any? = null
    private var schoolId:Any? = null
    private val answers = mutableMapOf<String, Boolean>()
    private val buttons = mutableListOf<Button>()
    private var literature = mutableListOf<OnlineSchools.Literature>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOnlineLessonFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[ SchoolViewModel::class.java]
        lessonId = arguments?.get("lessonId")
        schoolId = arguments?.get("schoolId")
        setListeners()
        setSystemUiVisibility()
        setObservers(this)
    }

    private fun setListeners() {

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineLessons.observe(owner, Observer {
            lessonId?.run {
                for (i in it.lessons.indices){
                    val lesson = it.lessons[i]
                    if(lesson.lessonId == this){
                        binding.textViewTitle.text = lesson.lessonTitle
                        val lessonNumber = "${getString(R.string.lesson_number)} ${lesson.lessonNumber}"
                        binding.textViewLessonNumber.text = lessonNumber
                        binding.textViewQuestion.text = lesson.testQuestion
                        return@Observer
                    }
                }
            }
        })
        schoolVM.onlineSchools.observe(owner, Observer {
            schoolId?.run {
                var onlineSchool: OnlineSchools.OnlineSchool? = null
                for(i in it.onlineSchools.indices){
                    if(it.onlineSchools[i].id.toString() == this ){
                        onlineSchool = it.onlineSchools[i]
                    }
                }
                onlineSchool?.run {
                    literature.addAll(this.literatures)
                    val container = binding.includeLiterature.linearLayoutLiterature
                    container.removeAllViews()
                    for (i in literature.indices) {
                        val view = LayoutLiteratureItemBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            container,
                            false
                        ).root
                        view.textView_title.text = literature[i].name
                        view.textView_pet.text = literature[i].pet
                        view.constraint_literure.setOnClickListener {
                            val newIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(literature[i].src)
                            )
                            startActivity(newIntent)
                        }
                        val margin = resources.getDimension(R.dimen.marginLessonNumber).toInt()
                        (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                            0,
                            margin,
                            margin,
                            0
                        )
                        container.addView(view)
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