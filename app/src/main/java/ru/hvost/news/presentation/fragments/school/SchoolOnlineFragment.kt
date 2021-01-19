package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_school_online.*
import kotlinx.android.synthetic.main.layout_lesson_number.view.*
import kotlinx.android.synthetic.main.layout_lesson_numbers.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentSchoolOnlineBinding
import ru.hvost.news.databinding.LayoutLessonNumberBinding
import ru.hvost.news.presentation.adapters.recycler.SchoolOnlineInfoAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolOnlineMaterialsAdapter
import ru.hvost.news.presentation.dialogs.SuccessRegistrationSchoolDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver


class SchoolOnlineFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var onlineSchoolsEvent: DefaultNetworkEventObserver
    private lateinit var onlineLessonsEvent: DefaultNetworkEventObserver
    private lateinit var navC: NavController
    private val materialsAdapter = SchoolOnlineMaterialsAdapter()
    private val infoAdapter = SchoolOnlineInfoAdapter()
    private var schoolId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOnlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("LABEL_NAME_CLASH")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        val title = arguments?.getString("schoolTitle")
        schoolVM.successRegistration.value?.run {
            if (this && title != null) {
                SuccessRegistrationSchoolDialog(title).show(
                    childFragmentManager,
                    "success_registration_dialog"
                )
                schoolVM.successRegistration.value = false
            }
        }
        navC = findNavController()
        val schoolId = arguments?.getString("schoolId")
        binding.recyclerView.adapter = materialsAdapter
        materialsAdapter.onClickLessonActive =
            object : SchoolOnlineMaterialsAdapter.OnClickLessonActive {
                override fun onClick(lessonId: String) {
                    val bundle = Bundle()
                    bundle.putString("schoolId", this@SchoolOnlineFragment.schoolId)
                    bundle.putString("lessonId", lessonId)
                    navC.navigate(
                        R.id.action_onlineCourseActiveFragment_to_onlineLessonActiveFragment,
                        bundle
                    )
                }
            }
        materialsAdapter.onClickLessonFinished =
            object : SchoolOnlineMaterialsAdapter.OnClickLessonFinished {
                override fun onClick(lessonId: String) {
                    val bundle = Bundle()
                    bundle.putString("schoolId", this@SchoolOnlineFragment.schoolId)
                    bundle.putString("lessonId", lessonId)
                    navC.navigate(
                        R.id.action_onlineCourseActiveFragment_to_onlineLessonFinishedFragment,
                        bundle
                    )
                }
            }

        schoolId?.let { id ->
            this.schoolId = id
            App.getInstance().userToken?.run {
                schoolVM.getOnlineLessons(this, id)
            }
            val schoolsResponse = schoolVM.onlineSchools.value
            schoolsResponse?.run {
                val schools = this.onlineSchools
                this@SchoolOnlineFragment.schoolId?.run {
                    for (i in schools.indices) {
                        val idSchool = schools[i].id.toString()
                        if (idSchool == this) {
                            val school = schools[i]
                            if (school.title.isNotBlank()) binding.textViewTitle.text = school.title
                            if (school.userRank.isNotBlank()) binding.textViewRank.text =
                                school.userRank
                            Glide.with(requireContext())
                                .load(APIService.baseUrl + school.imageDetailUrl)
                                .placeholder(R.drawable.empty_image).centerCrop()
                                .into(binding.imageViewLogo)
                            return@run
                        }
                    }
                }
            }
        }
        initializedEvents()
        setObservers(this)
        setListeners()
        App.getInstance().userToken?.run {
            schoolVM.getOnlineSchools(this)
        }
        schoolVM.onlineLessons.value?.lessons?.let{
            if (it.isEmpty()) binding.textViewEmpty.visibility = View.VISIBLE
            else binding.textViewEmpty.visibility = View.GONE
        }

    }

    private fun initializedEvents() {
        onlineSchoolsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.onlineSchools.value?.onlineSchools?.let { onlineSchools ->
                    schoolId?.run {
                        for (i in onlineSchools.indices) {
                            val onlineSchool = onlineSchools[i]
                            if (onlineSchool.id.toString() == this) {
                                infoAdapter.setSchool(onlineSchool)
                                materialsAdapter.setSchool(onlineSchool)

                                if (onlineSchool.participate) {
                                    binding.constraintRegistration.visibility = View.GONE
                                    binding.textViewEmpty.visibility = View.GONE
                                }
                                else {
                                    binding.constraintRegistration.visibility = View.VISIBLE
                                    binding.textViewEmpty.visibility = View.VISIBLE
                                }
                                val containerNumbers = linearLayout_lesson_numbers
                                val padding =
                                    resources.getDimension(R.dimen.logoOnlineSchoolPadding).toInt()
                                containerNumbers.setPadding(0, 0, 0, padding)
                                containerNumbers.removeAllViews()
                                for (q in onlineSchool.lessonsPassed.indices) {
                                    val number = (q + 1).toString()
                                    val isPassed = onlineSchool.lessonsPassed[q].isPassed
                                    val viewWait = LayoutLessonNumberBinding.inflate(
                                        LayoutInflater.from(requireContext()),
                                        containerNumbers,
                                        false
                                    ).root
                                    viewWait.textView_lesson_number.background =
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.selector_lesson_number
                                        )
                                    viewWait.textView_lesson_number.isSelected =
                                        onlineSchool.lessonsPassed[q].isPassed
                                    viewWait.textView_lesson_number.text = number
                                    viewWait.textView_lesson_number.isSelected = isPassed

                                    if (isPassed) viewWait.textView_lesson_number.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            android.R.color.white
                                        )
                                    )
                                    else viewWait.textView_lesson_number.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.gray3
                                        )
                                    )
                                    val paddingNormal = resources.getDimension(R.dimen._14dp).toInt()
                                    val paddingEdge =  resources.getDimension(R.dimen.largeMargin).toInt()
                                    if(q == 0 || q == onlineSchool.lessonsPassed.lastIndex){
                                        if(q == 0) {
                                            viewWait.setPadding(paddingEdge, 0,paddingNormal,0)
                                        }
                                        if (q == onlineSchool.lessonsPassed.lastIndex) {
                                            viewWait.setPadding(0, 0,paddingEdge,0)
                                        }
                                    }
                                    else {
                                        viewWait.setPadding(0, 0, paddingNormal,0)
                                    }
                                    containerNumbers.addView(viewWait)
                                }
                            }
                        }
                    }
                }
            }
        )

        onlineLessonsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.onlineLessons.value?.lessons?.let { lessons ->
                    materialsAdapter.setLessons(lessons)
                }
            }
        )
    }

    fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineLessonsEvent.observe(owner, onlineLessonsEvent)
        schoolVM.onlineSchoolsEvent.observe(owner, onlineSchoolsEvent)
        schoolVM.onlineSchools.observe(owner, {
            for(i in it.onlineSchools.indices){
                val school = it.onlineSchools[i]
                schoolId?.let { schoolId ->
                    if(school.id.toString() == schoolId){
                        if(school.participate) {
                            binding.constraintRegistration.visibility = View.GONE
                            binding.textViewEmpty.visibility = View.GONE }
                        else {
                            binding.constraintRegistration.visibility = View.VISIBLE
                            binding.textViewEmpty.visibility = View.VISIBLE
                        }

                    }
                }

            }
        })
    }

    private fun setListeners() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val colorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)
        binding.constraintMaterials.isSelected = true
        binding.schoolMaterialsTitle.setTextColor(colorWhite)
        binding.constraintInfo.setOnClickListener {
            it.isSelected = true
            constraint_materials.isSelected = false
            binding.recyclerView.adapter = infoAdapter
            binding.schoolInfoTitle.setTextColor(colorWhite)
            binding.schoolMaterialsTitle.setTextColor(colorPrimary)
            binding.textViewEmpty.visibility = View.GONE
        }
        binding.constraintMaterials.setOnClickListener {
            it.isSelected = true
            constraint_info.isSelected = false
            binding.recyclerView.adapter = materialsAdapter
            binding.schoolMaterialsTitle.setTextColor(colorWhite)
            binding.schoolInfoTitle.setTextColor(colorPrimary)
            schoolVM.onlineLessons.value.run {
                if (this == null || this.lessons.isEmpty()) binding.textViewEmpty.visibility = View.VISIBLE
                else binding.textViewEmpty.visibility = View.GONE
            }
        }
        binding.buttonRegistration.setOnClickListener {
            val bundle = Bundle()
            schoolId?.run {
                bundle.putString("schoolId", this)
            }
            findNavController().navigate(
                R.id.action_schoolOnlineActiveFragment_to_registrationFragment,
                bundle
            )
        }
        binding.toolbarOnlineSchool.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}