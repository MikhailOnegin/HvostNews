package ru.hvost.news.presentation.fragments.school


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_online_lesson_option.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonFinishedBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.startIntentActionView
import java.lang.StringBuilder

class LessonOnlineFinishedFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonFinishedBinding
    private lateinit var schoolVM: SchoolViewModel
    private var lessonId: Any? = null
    private var schoolId: Any? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOnlineLessonFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        lessonId = arguments?.get("lessonId")
        schoolId = arguments?.get("schoolId")
        setListeners()
        setObservers(this)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineSchools.observe(owner, {
            schoolId?.run {
                var onlineSchool: OnlineSchools.OnlineSchool? = null
                for (i in it.onlineSchools.indices) {
                    if (it.onlineSchools[i].id.toString() == this) {
                        onlineSchool = it.onlineSchools[i]
                    }
                }
                onlineSchool?.run {
                    val literature = this.literature
                    val container = binding.includeLiterature.linearLayoutLiterature
                    container.removeAllViews()
                    if (literature.isNotEmpty()) {
                        for (i in literature.indices) {
                            val viewLiterature = LayoutLiteratureItemBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                container,
                                false
                            ).root
                            viewLiterature.textView_title.text = literature[i].title
                            viewLiterature.textView_pet.text = literature[i].pet
                            viewLiterature.constraint_literature.setOnClickListener {
                                startIntentActionView(
                                    requireContext(),
                                    literature[i].fileUrl
                                )
                            }
                            val margin = resources.getDimension(R.dimen.largeMargin).toInt()

                            if(i == this.literature.lastIndex) {
                                (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                                    margin,
                                    0,
                                    margin,
                                    0
                                )
                            }
                            else {
                                (viewLiterature.layoutParams as LinearLayout.LayoutParams).setMargins(
                                    margin,
                                    0,
                                    0,
                                    0
                                )
                            }
                            container.addView(viewLiterature)
                        }
                    } else binding.includeLiterature.rootConstraint.visibility = View.GONE
                }
            }
        })
        schoolVM.onlineLessons.observe(owner, {
            lessonId.let { lessonId ->
                for (i in it.lessons.indices) {
                    val lesson = it.lessons[i]
                    if (lesson.lessonId == lessonId) {
                        binding.textViewTitle.text = lesson.lessonTitle
                        val lessonNumber =
                            "${getString(R.string.lesson_number)} ${lesson.lessonNumber}"
                        binding.textViewLessonNumber.text = lessonNumber
                        binding.textViewQuestion.text = lesson.testQuestion
                        Glide.with(requireContext()).load(baseUrl + lesson.imageVideoUrl)
                            .placeholder(R.drawable.not_found).centerCrop()
                            .into(binding.imageViewVideo)
                        for (q in lesson.answerList.indices) {
                            val answer = lesson.answerList[q]
                            if (answer.isTrue) {
                                if (answer.answer == "Все вышеперечисленное") {
                                    val rightAnswers = StringBuilder()
                                    for(j in lesson.answerList.indices){
                                        val answer2 = lesson.answerList[j].answer
                                        if(answer2 != "Все вышеперечисленное"){
                                            rightAnswers.append("$answer2\n\n")
                                        }
                                    }
                                    binding.textViewRightAnswer.text = rightAnswers.toString()
                                } else {
                                    binding.textViewRightAnswer.text = answer.answer
                                    return@observe
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}