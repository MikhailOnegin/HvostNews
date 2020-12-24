package ru.hvost.news.presentation.fragments.school

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.startIntent


class LessonOnlineActiveFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonActiveBinding
    private lateinit var schoolVM: SchoolViewModel
    private var lessonId: String? = null
    private var schoolId: String? = null
    private val answers = mutableMapOf<String, Boolean>()
    private val buttons = mutableListOf<Button>()
    private var lesson: OnlineLessons.OnlineLesson? = null
    private var lessons: List<OnlineLessons.OnlineLesson>? = null
    private lateinit var lessonTestPassedEvent: DefaultNetworkEventObserver
    private lateinit var onlineLessonsEvent: DefaultNetworkEventObserver
    private lateinit var onlineSchoolsEvent: DefaultNetworkEventObserver

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
        initializedEvents()
        setListeners()
        setObservers(this)
    }

    private fun initializedEvents() {
        lessonTestPassedEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = {
                binding.buttonToAnswer.isEnabled = false
            },
            doOnSuccess = {
                binding.buttonToAnswer.isEnabled = true
                binding.buttonToAnswer.text = resources.getString(R.string.next_lesson)
                binding.buttonToAnswer.setOnClickListener {
                    lessons?.let { lessons ->
                        lessonId?.let { lessonId ->
                            for (i in lessons.indices) {
                                val lesson = lessons[i]
                                if (lesson.lessonId == lessonId) {
                                    if (i < lessons.size - 2) {
                                        schoolId?.let { schoolId ->
                                            val bundle = Bundle()
                                            bundle.putString("lessonId", lessonId)
                                            bundle.putString("schoolId", schoolId)
                                            findNavController().navigate(
                                                R.id.action_onlineLessonFragment_toOnlineLessonFragment,
                                                bundle
                                            )
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Last lesson",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            },
            doOnError = {
                binding.buttonToAnswer.isEnabled = true
            },
            doOnFailure = {
                binding.buttonToAnswer.isEnabled = true
            }
        )
        onlineLessonsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.onlineLessons.value?.lessons?.let { onlineLessons ->
                    lessons = onlineLessons
                    if (onlineLessons.isNotEmpty()) {
                        lessonId?.run {
                            for (i in onlineLessons.indices) {
                                val onlineLesson = onlineLessons[i]
                                if (onlineLesson.lessonId == this) {
                                    lesson = onlineLesson
                                    binding.textViewTitle.text = onlineLesson.lessonTitle
                                    val lessonNumber =
                                        "${getString(R.string.lesson_number)} ${onlineLesson.lessonNumber}"
                                    binding.textViewLessonNumber.text = lessonNumber
                                    binding.textViewQuestion.text = onlineLesson.testQuestion
                                    binding.imageViewPlay.setOnClickListener {
                                        startIntent(requireContext(), onlineLesson.videoUrl)
                                    }
                                    binding.constraintVideo.setOnClickListener {
                                        startIntent(requireContext(), onlineLesson.videoUrl)
                                    }
                                    Glide.with(requireContext()).load(onlineLesson.imageVideoUrl)
                                        .placeholder(R.drawable.not_found).centerCrop()
                                        .into(binding.imageViewVedeo)

                                    val containerOptions = binding.linearLayoutAnswerOptions
                                    containerOptions.removeAllViews()
                                    for (q in onlineLesson.answerList.indices) {
                                        val answer = onlineLesson.answerList[q]
                                        answers[answer.answer] = answer.isTrue
                                        val view = LayoutOnlineLessonOptionBinding.inflate(
                                            LayoutInflater.from(requireContext()),
                                            containerOptions,
                                            false
                                        ).root
                                        buttons.add(view.button_option)
                                        view.button_option.text = answer.answer
                                        view.button_option.setOnClickListener {
                                            schoolVM.selectLessonAnswersCount.value?.run {
                                                if (it.isSelected) schoolVM.selectLessonAnswersCount.value =
                                                    this - 1
                                                else schoolVM.selectLessonAnswersCount.value =
                                                    this + 1
                                            }
                                            it.isSelected = !it.isSelected
                                        }
                                        val margin =
                                            resources.getDimension(R.dimen.smallMargin).toInt()
                                        (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                                            0,
                                            margin,
                                            0,
                                            0
                                        )
                                        containerOptions.addView(view)
                                    }
                                    return@run
                                }
                            }
                        }
                    } else {
                        createSnackbar(
                            anchorView = binding.root,
                            text = "Произошла ошибка (ответы не загружены)",
                            resources.getString(R.string.buttonOk),
                        ).show()
                    }
                }
            }
        )

        onlineSchoolsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.onlineSchools.value?.onlineSchools?.let { onlineSchools ->
                    schoolId?.run {
                        var onlineSchool: OnlineSchools.OnlineSchool? = null
                        for (i in onlineSchools.indices) {
                            if (onlineSchools[i].id.toString() == this) {
                                onlineSchool = onlineSchools[i]
                            }
                        }
                        onlineSchool?.run {
                            val literature = this.literature
                            val container = binding.includeLiterature.linearLayoutLiterature
                            container.removeAllViews()
                            if (literature.isNotEmpty()) {
                                for (i in literature.indices) {
                                    val view = LayoutLiteratureItemBinding.inflate(
                                        LayoutInflater.from(requireContext()),
                                        container,
                                        false
                                    ).root
                                    view.textView_title.text = literature[i].title
                                    view.textView_pet.text = literature[i].pet
                                    view.constraint_literure.setOnClickListener {
                                        startIntent(requireContext(), literature[i].fileUrl)
                                    }
                                    val margin =
                                        resources.getDimension(R.dimen.marginLessonNumber).toInt()
                                    (view.layoutParams as LinearLayout.LayoutParams).setMargins(
                                        0,
                                        margin,
                                        margin,
                                        0
                                    )
                                    container.addView(view)
                                }
                            } else binding.includeLiterature.constraintRoot.visibility = View.GONE
                        }
                    }
                }
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.lessonTestesPassedEvent.observe(owner, lessonTestPassedEvent)
        schoolVM.onlineLessonsEvent.observe(owner, onlineLessonsEvent)
        schoolVM.onlineSchoolsEvent.observe(owner, onlineSchoolsEvent)
        schoolVM.selectLessonAnswersCount.observe(owner, {
            binding.buttonToAnswer.isEnabled = it > 0
        })
    }

    private fun setListeners() {
        binding.buttonToAnswer.setOnClickListener { buttonAnswer ->
            for (i in buttons.indices) {
                val button = buttons[i]
                val isTrue = answers[button.text.toString()]
                isTrue?.run {
                    if (this) {
                        button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                    }
                    else{
                        if(button.isSelected) {
                            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                            button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        }
                    }

                }
            }
            App.getInstance().userToken?.let { userToken ->
                lessonId?.let { lessonId ->
                    schoolVM.setLessonTestesPassed(userToken, lessonId.toLong())
                }
            }
        }

        binding.toolbarOnlineLesson.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}