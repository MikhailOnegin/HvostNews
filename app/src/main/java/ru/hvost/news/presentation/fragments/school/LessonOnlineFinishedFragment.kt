package ru.hvost.news.presentation.fragments.school


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_online_lesson_option.view.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOnlineLessonFinishedBinding
import ru.hvost.news.databinding.LayoutLiteratureItemBinding
import ru.hvost.news.databinding.LayoutOnlineLessonOptionBinding
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.startIntent

class LessonOnlineFinishedFragment: BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineLessonFinishedBinding
    private lateinit var schoolVM: SchoolViewModel
    private var lessonId:Any? = null
    private var schoolId:Any? = null
    private var lesson: OnlineLessons.OnlineLesson? = null
    private var lessons: List<OnlineLessons.OnlineLesson>? = null
    private val answers = mutableMapOf<String, Boolean>()
    private val buttons = mutableListOf<Button>()
    private var literature = mutableListOf<OnlineSchools.Literature>()
    private lateinit var onlineLessonsEvent:DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOnlineLessonFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[ SchoolViewModel::class.java]
        lessonId = arguments?.get("lessonId")
        schoolId = arguments?.get("schoolId")
        initializedEvents()
    }

    private fun initializedEvents() {
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
                                    val lessonNumber = "${getString(R.string.lesson_number)} ${onlineLesson.lessonNumber}"
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
                                        .into(binding.imageViewVideo)
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
    }

}