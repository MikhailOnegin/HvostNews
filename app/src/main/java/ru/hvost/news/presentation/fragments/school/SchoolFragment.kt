package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_lesson_number.view.*
import kotlinx.android.synthetic.main.layout_lesson_numbers.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentSchoolBinding
import ru.hvost.news.databinding.LayoutLessonNumberBinding
import ru.hvost.news.presentation.dialogs.SchoolSuccessRegistrationDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import java.lang.Exception


class SchoolFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navCMain: NavController
    private lateinit var navCSchool: NavController
    private var schoolId: String? = null
    private var fromDestination: String? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("LABEL_NAME_CLASH")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        val title = arguments?.getString("schoolTitle")
        schoolVM.successRegistration.value?.run {
            if (this && title != null) {
                App.getInstance().userToken?.let {
                    schoolVM.getSchools(it)
                }
                SchoolSuccessRegistrationDialog(title).show(
                        childFragmentManager,
                        "success_registration_dialog"
                )
            }
            schoolVM.successRegistration.value = null
        }
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        navCSchool = requireActivity().findNavController(R.id.fragmentContainerSchool)
        schoolId = arguments?.getString("schoolId")
        schoolId?.let {
            this.schoolId = it
            val schoolsResponse = schoolVM.onlineSchools.value
            schoolsResponse?.run {
                val schools = this.onlineSchools
                this@SchoolFragment.schoolId?.run {
                    for (i in schools.indices) {
                        val idSchool = schools[i].id.toString()
                        if (idSchool == this) {
                            val school = schools[i]
                            if (school.title.isNotBlank()) binding.textViewTitle.text = school.title
                            if (school.userRank.isNotBlank()) binding.textViewRank.text =
                                school.userRank
                            Glide.with(requireContext())
                                .load(APIService.baseUrl + school.imageDetailUrl)
                                .placeholder(R.drawable.loader_anim)
                                .error(R.drawable.ic_load_error)
                                .centerCrop()
                                .into(binding.imageViewLogo)
                            return@run
                        }
                    }
                }
            }
        }
        setListeners()
        setObservers(this)
        App.getInstance().userToken?.let { userToken ->
            schoolId?.let { schoolId ->
                schoolVM.getSchoolLessons(userToken, schoolId )
            }
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set("fromDestination","school")
        setTabsSelected(fromDestination)
    }


    fun setObservers(owner: LifecycleOwner) {

        schoolVM.onlineSchools.observe(owner, { schoolsResponse ->
            schoolId?.let { schoolId ->
                for (i in schoolsResponse.onlineSchools.indices) {
                    val onlineSchool = schoolsResponse.onlineSchools[i]
                    if (onlineSchool.id.toString() == schoolId) {
                        if (onlineSchool.participate) {
                            binding.constraintRegistration.visibility = View.GONE
                            binding.constraintRank.visibility = View.VISIBLE
                        } else {
                            binding.constraintRegistration.visibility = View.VISIBLE
                            binding.constraintRank.visibility = View.INVISIBLE
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
                            val paddingNormal =
                                resources.getDimension(R.dimen._14dp).toInt()
                            val paddingEdge =
                                resources.getDimension(R.dimen.largeMargin).toInt()
                            if (q == 0 || q == onlineSchool.lessonsPassed.lastIndex) {
                                if (q == 0) {
                                    viewWait.setPadding(paddingEdge, 0, paddingNormal, 0)
                                }
                                if (q == onlineSchool.lessonsPassed.lastIndex) {
                                    viewWait.setPadding(0, 0, paddingEdge, 0)
                                }
                            } else {
                                viewWait.setPadding(0, 0, paddingNormal, 0)
                            }
                            containerNumbers.addView(viewWait)
                        }
                        return@observe
                    }
                }
            }
        })
        fromDestination = navCMain.currentBackStackEntry?.savedStateHandle?.get<String>("fromDestination")
    }

    private fun setListeners() {
        binding.tabInfo.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.tabMaterials.isSelected = false
                try {
                    navCSchool.navigate(R.id.action_schoolMaterialsFragment_to_schoolInfoFragment)
                }
                catch (e:Exception){
                }
            }
        }
        binding.tabMaterials.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.tabInfo.isSelected = false
                try {
                    navCSchool.navigate(R.id.action_schoolInfoFragment_to_schoolMaterialsFragment)
                }
                catch (e:Exception){
                }
            }
        }
        binding.buttonRegistration.setOnClickListener {
            val bundle = Bundle()
            schoolId?.run {
                bundle.putString("schoolId", this)
            }
            findNavController().navigate(
                    R.id.action_schoolFragment_to_registrationSchoolFragment,
                    bundle
            )
        }
        binding.toolbarOnlineSchool.setNavigationOnClickListener {
            navCMain.popBackStack()
        }
    }

    private fun setTabsSelected(fromDestination: String?) {
        when (fromDestination) {
            "lastLesson" -> {
                binding.rootCoordinator.scrollTo(0, 0)
                binding.tabInfo.isSelected = false
                binding.tabMaterials.isSelected = true
            }
            "lesson" -> {
                binding.tabInfo.isSelected = false
                binding.tabMaterials.isSelected = true
            }
            else -> {
                binding.tabInfo.isSelected = true
                binding.tabMaterials.isSelected = false
            }
        }
    }
}