package ru.hvost.news.presentation.fragments.school


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.FragmentSchoolLessonFinishedBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getDefaultShimmer
import ru.hvost.news.utils.startIntentActionView
import java.lang.StringBuilder

class LessonFinishedFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolLessonFinishedBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navCMain: NavController
    private var lessonId: Any? = null
    private var schoolId: Any? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolLessonFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        navCMain.previousBackStackEntry?.savedStateHandle?.set("fromDestination", "lesson")
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
        schoolVM.onlineLessons.observe(owner, {
            lessonId.let { lessonId ->
                for (i in it.lessons.indices) {
                    val lesson = it.lessons[i]
                    if (lesson.lessonId == lessonId) {
                        binding.textViewTitle.text = lesson.lessonTitle
                        val lessonNumber =
                            "${getString(R.string.lesson_number)} ${i + 1}"
                        binding.textViewLessonNumber.text = lessonNumber
                        binding.textViewQuestion.text = lesson.testQuestion
                        Glide.with(requireContext())
                                .load(baseUrl + lesson.imageVideoUrl)
                                .placeholder(getDefaultShimmer(requireContext()))
                                .error(R.drawable.empty_image)
                                .centerCrop()
                                .into(binding.imageViewVideo)
                        for (q in lesson.answerList.indices) {
                            val answer = lesson.answerList[q]
                            if (answer.isTrue) {
                                if (answer.answer == getString(R.string.all_listed)) {
                                    val rightAnswers = StringBuilder()
                                    for(j in lesson.answerList.indices){
                                        val answer2 = lesson.answerList[j].answer
                                        if(answer2 != getString(R.string.all_listed)){
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

                    //add literature
                    val literature = lesson.literatures
                    val container = binding.includeLiterature.linearLayoutLiterature
                    container.removeAllViews()
                    if (literature.isNotEmpty()) {
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
                                        literature[q].fileUrl
                                )
                            }
                            val paddingNormal = resources.getDimension(R.dimen.normalMargin).toInt()
                            val paddingEdge = resources.getDimension(R.dimen.largeMargin).toInt()

                            if(q == 0 || q == literature.lastIndex){
                                if (q == 0) viewLiterature.setPadding(paddingEdge, 0,paddingNormal,0)
                                else if (q == literature.lastIndex) viewLiterature.setPadding(0, 0,paddingEdge,0)
                            }
                            else viewLiterature.setPadding(0, 0, paddingNormal,0)
                            container.addView(viewLiterature)
                        }
                    } else binding.includeLiterature.rootConstraint.visibility = View.GONE
                }
            }
        })
    }
}