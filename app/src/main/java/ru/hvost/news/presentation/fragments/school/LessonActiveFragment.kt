package ru.hvost.news.presentation.fragments.school

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_lesson_option.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.FragmentSchoolLessonActiveBinding
import ru.hvost.news.databinding.LayoutLessonOptionBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.dialogs.SchoolFinishDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent
import ru.hvost.news.utils.getDefaultShimmer
import ru.hvost.news.utils.startIntentActionView


class LessonActiveFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolLessonActiveBinding
    private lateinit var schoolVM: SchoolViewModel
    private var school:OnlineSchools.OnlineSchool? = null
    private var lessonId: String? = null
    private var schoolId: String? = null
    private val answers = mutableMapOf<String, Boolean>()
    private val buttons = mutableListOf<Button>()
    private var lesson: OnlineLessons.OnlineLesson? = null
    private var lessons: List<OnlineLessons.OnlineLesson>? = null
    private lateinit var lessonTestPassedEvent: DefaultNetworkEventObserver
    private lateinit var navCMain: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolLessonActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = findNavController()
        lessonId = arguments?.getString("lessonId")
        schoolId = arguments?.getString("schoolId")
        navCMain.previousBackStackEntry?.savedStateHandle?.set("fromDestination", "lesson")
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
                App.getInstance().userToken?.let {
                    schoolVM.getSchools(it)
                }
                binding.buttonToAnswer.isEnabled = true
                lessons?.let { lessons ->
                    lesson?.let {  lesson ->
                    if (lessons.last().lessonId == lesson.lessonId) {
                        binding.buttonToAnswer.isEnabled = true
                        binding.buttonToAnswer.text = resources.getString(R.string.finish)
                        binding.buttonToAnswer.setOnClickListener {
                            navCMain.previousBackStackEntry?.savedStateHandle?.set("fromDestination", "lastLesson")
                            navCMain.popBackStack()
                        }
                        school?.let {
                            SchoolFinishDialog(it.title).show(
                                    childFragmentManager,
                                    "success_registration_dialog"
                            )
                        }

                    } else {
                        binding.buttonToAnswer.text = resources.getString(R.string.next_lesson)
                        binding.buttonToAnswer.setOnClickListener {
                            lessonId?.let { lessonId ->
                                for (i in lessons.indices) {
                                    if (lessons[i].lessonId == lessonId) {
                                        if (i < lessons.size - 1) {
                                            val nextLesson = lessons[i + 1]
                                            schoolId?.let { schoolId ->
                                                val bundle = Bundle()
                                                bundle.putString("lessonId", nextLesson.lessonId)
                                                bundle.putString("schoolId", schoolId)
                                                findNavController().navigate(
                                                    R.id.action_onlineLessonFragment_toOnlineLessonFragment,
                                                    bundle
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            }
                        }
                    }
                }
                for (i in buttons.indices) {
                    buttons[i].isEnabled = false
                }
            },
            doOnError = {
                binding.buttonToAnswer.isEnabled = true
            },
            doOnFailure = {
                binding.buttonToAnswer.isEnabled = true
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.lessonTestesPassedEvent.observe(owner, lessonTestPassedEvent)
        schoolVM.onlineSchools.value?.let { onlineSchools ->
            for (i in onlineSchools.onlineSchools.indices){
                val school = onlineSchools.onlineSchools[i]
                schoolId?.let { schoolId ->
                    if(school.id.toString() == schoolId){
                        this.school = school
                    }
                }
            }
        }
        schoolVM.onlineLessons.observe(owner, {
            lessons = it.lessons
            if (it.lessons.isNotEmpty()) {
                lessonId?.run {
                    for (i in it.lessons.indices) {
                        val lesson = it.lessons[i]
                        if (lesson.lessonId == this) {
                            this@LessonActiveFragment.lesson = lesson
                            // add literature
                            val literature = lesson.literatures
                            val container = binding.includeLiterature.linearLayoutLiterature
                            container.removeAllViews()
                            if (literature.isNotEmpty()) {
                                binding.includeLiterature.rootConstraint.visibility = View.VISIBLE
                                for (q in literature.indices) {
                                    val viewLiterature = LayoutLiteratureItemBinding.inflate(
                                            LayoutInflater.from(requireContext()),
                                            container,
                                            false
                                    ).root
                                    viewLiterature.textView_title.text = literature[q].title
                                    viewLiterature.textView_pet.text = literature[q].pet
                                    viewLiterature.constraint_literature.setOnClickListener {
                                        startIntentActionView(
                                                requireContext(),
                                                baseUrl + literature[q].fileUrl
                                        )
                                    }
                                    val paddingNormal = resources.getDimension(R.dimen.normalMargin).toInt()
                                    val paddingEdge = resources.getDimension(R.dimen.largeMargin).toInt()

                                    if (q == 0 || q == literature.lastIndex) {
                                        if (q == 0) viewLiterature.setPadding(
                                                paddingEdge,
                                                0,
                                                paddingNormal,
                                                0
                                        )
                                        else if (q == literature.lastIndex) viewLiterature.setPadding(
                                                0,
                                                0,
                                                paddingEdge,
                                                0
                                        )
                                    } else viewLiterature.setPadding(0, 0, paddingNormal, 0)
                                    container.addView(viewLiterature)
                                }
                            } else binding.includeLiterature.rootConstraint.visibility = View.GONE
                            //
                            binding.textViewTitle.text = lesson.lessonTitle
                            val lessonNumber =
                                "${getString(R.string.lesson_number)} ${i + 1}"
                            binding.textViewLessonNumber.text = lessonNumber
                            binding.textViewQuestion.text = lesson.testQuestion
                            binding.imageViewPlay.setOnClickListener {
                                startIntentActionView(requireContext(), lesson.videoUrl)
                            }
                            binding.constraintVideo.setOnClickListener {
                                startIntentActionView(requireContext(), lesson.videoUrl)
                            }
                            Glide.with(requireContext())
                                    .load(baseUrl + lesson.imageVideoUrl)
                                    .placeholder(getDefaultShimmer(requireContext()))
                                    .error(R.drawable.empty_image)
                                    .centerCrop()
                                    .into(binding.imageViewVideo)

                            val containerOptions = binding.linearLayoutAnswerOptions
                            containerOptions.removeAllViews()
                            for (q in lesson.answerList.indices) {
                                val answer = lesson.answerList[q]
                                answers[answer.answer] = answer.isTrue
                                val viewOption = LayoutLessonOptionBinding.inflate(
                                    LayoutInflater.from(requireContext()),
                                    containerOptions,
                                    false
                                ).root
                                buttons.add(viewOption.button_option)
                                viewOption.button_option.text = answer.answer
                                viewOption.button_option.setOnClickListener {
                                    for (c in buttons.indices) {
                                        buttons[c].isSelected = false
                                    }
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
                                (viewOption.layoutParams as LinearLayout.LayoutParams).setMargins(
                                    0,
                                    margin,
                                    0,
                                    0
                                )
                                containerOptions.addView(viewOption)
                            }
                            return@run
                        }
                    }
                }
            } else {
                createSnackbar(
                    anchorView = binding.root,
                    text = resources.getString(R.string.unknown_error),
                    resources.getString(R.string.buttonOk),
                ).show()
            }
            schoolVM.sendLessonReadyEvent()
        })
        schoolVM.selectLessonAnswersCount.observe(owner, {
            val selected = it > 0
            if (selected) {
                binding.buttonToAnswer.isEnabled = selected
                schoolVM.selectLessonAnswersCount.value = 0
            }

        })
        schoolVM.lessonReadyEvent.observe(owner) { onLessonReadyEvent(it) }
    }

    private fun setListeners() {
        binding.buttonToAnswer.setOnClickListener {
            var answerSelected = false
            for (i in buttons.indices) {
                val button = buttons[i]
                if (button.isSelected) answerSelected = true
                val isTrue = answers[button.text.toString()]
                isTrue?.run {
                    if (this) {
                        button.backgroundTintList = ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.colorPrimary
                        )
                        button.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                    }
                    if (button.isSelected && !this) {
                        button.backgroundTintList =
                            ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.red
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
            if (answerSelected) {
                App.getInstance().userToken?.let { userToken ->
                    lessonId?.let { lessonId ->
                        schoolVM.setLessonTestesPassed(userToken, lessonId.toLong())
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.not_answers_selected), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onLessonReadyEvent(event: OneTimeEvent?) {
        event?.getEventIfNotHandled()?.run {
            binding.progress.visibility = View.GONE
            ObjectAnimator.ofFloat(
                binding.rootCoordinator,
                "alpha",
                0f  , 1f
            ).apply {
                duration = 300L
            }.start()
        }
    }
}
