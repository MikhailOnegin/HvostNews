package ru.hvost.news.presentation.fragments.school

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver


class SchoolOnlineFragment :  BaseFragment() {

    private lateinit var binding: FragmentSchoolOnlineBinding
    private lateinit var schoolVM: SchoolViewModel
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
        navC = findNavController()
        val id = arguments?.getString("schoolId")
        binding.recyclerView.adapter = materialsAdapter
        materialsAdapter.onClickLessonActive = object : SchoolOnlineMaterialsAdapter.OnClickLessonActive {
            override fun onClick(lessonId:String) {
                val bundle = Bundle()
                bundle.putString("schoolId", schoolId)
                bundle.putString("lessonId", lessonId)
                navC.navigate(R.id.action_onlineCourseActiveFragment_to_onlineLessonFragment, bundle)
            }
        }
        materialsAdapter.onClickLiterature = object : SchoolOnlineMaterialsAdapter.OnClickLiterature{
            override fun onClick(url: String) {
                val newIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                startActivity(newIntent)
            }
        }
        infoAdapter.onClickLiterature = object :SchoolOnlineInfoAdapter.OnClickLiterature{
            override fun onClick(url: String) {
                val newIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                startActivity(newIntent)
            }
        }
        setObservers(this)
        id?.let {id ->
            schoolId = id
            App.getInstance().userToken?.run {
                schoolVM.getOnlineLessons(this, id)
            }
            val schoolsResponse = schoolVM.onlineSchools.value
            schoolsResponse?.run {
                val schools = this.onlineSchools
                schoolId?.run {
                    for (i in schools.indices) {
                        val idSchool = schools[i].id.toString()
                        if (idSchool == this) {
                            val school = schools[i]
                            if (school.title.isNotBlank()) binding.textViewTitle.text = school.title
                            if (school.userRank.isNotBlank()) binding.textViewRank.text = school.userRank
                            Glide.with(requireContext()).load(APIService.baseUrl + school.imageDetailUrl)
                                .placeholder(R.drawable.not_found).centerCrop()
                                .into(binding.imageViewLogo)
                            return@run
                        }
                    }
                }
            }
        }
        setListeners()
    }

    fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineLessons.observe(owner, {
            materialsAdapter.setLessons(it.lessons)
        })
        schoolVM.onlineSchools.observe(owner, {
            schoolId?.run {
                for (i in it.onlineSchools.indices) {
                    val onlineSchool = it.onlineSchools[i]

                    if (onlineSchool.id.toString() == this) {
                        infoAdapter.setSchool(onlineSchool)
                        materialsAdapter.setSchool(onlineSchool)

                        if (onlineSchool.participate) binding.constraintRegistration.visibility =
                            View.GONE
                        else {
                            binding.constraintRegistration.visibility = View.VISIBLE
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
                        }

                        val containerNumbers = linearLayout_lesson_numbers
                        val padding =
                            resources.getDimension(R.dimen.logoOnlineSchoolPadding).toInt()
                        containerNumbers.setPadding(0, 0, 0, padding)

                        for (q in onlineSchool.lessonsPassed.indices) {
                            val number = (q + 1).toString()
                            val isPassed = onlineSchool.lessonsPassed[q].isPassed
                            val viewWait = LayoutLessonNumberBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                containerNumbers,
                                false
                            ).root
                            viewWait.textView_lesson_number.background = ContextCompat.getDrawable(
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
                            val margin = resources.getDimension(R.dimen.marginLessonNumber).toInt()
                            (viewWait.layoutParams as LinearLayout.LayoutParams).setMargins(
                                0,
                                0,
                                margin,
                                0
                            )
                            containerNumbers.addView(viewWait)
                        }
                    }
                }
            }
        })
        schoolVM.setParticipateEvent.observe(owner, {
            if (it.getContentIfNotHandled() == State.SUCCESS) {
                Toast.makeText(requireContext(), "Вы записаны на урок", Toast.LENGTH_SHORT).show()
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
        }
        binding.constraintMaterials.setOnClickListener {
            it.isSelected = true
            constraint_info.isSelected = false
            binding.recyclerView.adapter = materialsAdapter
            binding.schoolMaterialsTitle.setTextColor(colorWhite)
            binding.schoolInfoTitle.setTextColor(colorPrimary)
        }
        binding.toolbarOnlineSchool.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}