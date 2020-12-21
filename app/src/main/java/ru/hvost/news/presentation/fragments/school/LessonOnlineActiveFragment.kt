package ru.hvost.news.presentation.fragments.school

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_online_lesson_option.view.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonActiveBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.databinding.LayoutOnlineLessonOptionBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class LessonOnlineActiveFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonActiveBinding
    private lateinit var schoolVM: SchoolViewModel
    private var lessonId: String? = null
    private var schoolId: String? = null
    private val answers = mutableMapOf<String, Boolean>()
    private val buttons = mutableListOf<Button>()
    private var literature = mutableListOf<OnlineSchools.Literature>()
    private var lesson: OnlineLessons.OnlineLesson? = null
    private var lessons: List<OnlineLessons.OnlineLesson>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOnlineLessonActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        lessonId = arguments?.getString("lessonId")
        schoolId = arguments?.getString("schoolId")
        setListeners()
        setObservers(this)
    }

    private fun setListeners() {
        binding.buttonToAnswer.setOnClickListener {
            for (i in 0 until buttons.size) {
                val button = buttons[i]
                button.isEnabled = false
                val answer = answers[button.text.toString()]
                answer?.run {
                    if (this) {
                        button.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                        button.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                    } else {
                        button.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        button.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                    }
                }
            }
            lessonId?.run {
                val id = this
                App.getInstance().userToken?.run {
                    schoolVM.setLessonTestesPassed(
                        this,
                        id.toLong()
                    )
                }

            }


        }
        binding.imageViewPlay.setOnClickListener {

            lesson?.videoUrl?.run {

                val newIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(this)
                )
                startActivity(newIntent)
            }
        }
        binding.constraintVideo.setOnClickListener {

            lesson?.videoUrl?.run {
                val newIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(this)
                )
                startActivity(newIntent)
            }
        }

        binding.toolbarOnlineLesson.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setObservers(owner: LifecycleOwner) {

        schoolVM.lessonTestesPassedEvent.observe(owner, {

            binding.buttonToAnswer.text = resources.getString(R.string.next_lesson)
            binding.buttonToAnswer.setOnClickListener {
                lessons?.let {
                    for (i in it.indices) {
                        if (i < it.size - 1) {
                            //val lesson = it[i+1]
                            lessonId?.let {
                            }
                        }
                    }
                }
            }
        })
        schoolVM.onlineLessons.observe(owner, Observer {
            lessons = it.lessons
            lessonId?.run {
                for (i in it.lessons.indices) {
                    val onlineLesson = it.lessons[i]
                    if (onlineLesson.lessonId == this) {
                        lesson = onlineLesson
                        binding.textViewTitle.text = onlineLesson.lessonTitle
                        val lessonNumber =
                            "${getString(R.string.lesson_number)} ${onlineLesson.lessonNumber}"
                        binding.textViewLessonNumber.text = lessonNumber
                        binding.textViewQuestion.text = onlineLesson.testQuestion

                        val container = binding.linearLayoutAnswerOptions
                        for (q in onlineLesson.answersList.indices) {
                            val answer = onlineLesson.answersList[q]
                            answers[answer.answer] = answer.isTrue
                            val view = LayoutOnlineLessonOptionBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                container,
                                false
                            ).root
                            buttons.add(view.button_option)
                            view.button_option.text = answer.answer
                            view.button_option.setOnClickListener {
                                if (view.button_option.isEnabled) {
                                    view.button_option.isActivated = !view.button_option.isActivated
                                }
                            }
                            val margin = resources.getDimension(R.dimen.smallMargin).toInt()
                            (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                                0,
                                margin,
                                0,
                                0
                            )
                            container.addView(view)
                        }
                        return@Observer
                    }
                }
            }
        })
        schoolVM.onlineSchools.observe(owner, {
            schoolId?.run {
                var onlineSchool: OnlineSchools.OnlineSchool? = null
                for (i in it.onlineSchools.indices) {
                    if (it.onlineSchools[i].id.toString() == this) {
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
                        view.textView_title.text = literature[i].title
                        view.textView_pet.text = literature[i].pet
                        view.constraint_literure.setOnClickListener {
                            val newIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(literature[i].fileUrl)
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
}