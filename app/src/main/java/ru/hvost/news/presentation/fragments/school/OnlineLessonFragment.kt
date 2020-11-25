package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
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
        setListeners()
        setSystemUiVisibility()
        setObservers(this)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.onlineLessons.observe(owner, Observer {
            Log.i("eeee", "OnlineLessons ${it.lessons.size}")
            lessonId?.run {
                Log.i("eeee", "Lesson id is ${lessonId}")
                for (i in it.lessons.indices){
                    val lesson = it.lessons[i]
                    if(lesson.lessonId == this){
                        binding.textViewTitle.text = lesson.lessonTitle
                        val lessonNumber = "${getString(R.string.lesson_number)} ${lesson.lessonNumber}"
                        binding.textViewLessonNumber.text = lessonNumber
                        binding.textViewQuestion.text = lesson.testQuestion

                        //for test
                        val literatures = arrayListOf<OnlineSchoolsResponse.Literature>()
                        for (i in 0..10) {
                            val literature = OnlineSchoolsResponse.Literature("Нет апи", "Нет апи", "Нет апи")
                            literatures.add(literature)
                        }
                        val container = binding.includeLiterature.linearLayout
                        container.removeAllViews()
                        for (i in literatures.indices) {
                            val view = LayoutLiteratureItemBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                container,
                                false
                            ).root

                            view.textView_title.text = literatures[i].title
                            view.textView_pet.text = literatures[i].pet
                            val margin = resources.getDimension(R.dimen.normalMargin).toInt()
                            (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                                0,
                                margin,
                                margin + margin,
                                0
                            )
                            container.addView(view)
                        }

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