package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_school_online.*
import kotlinx.android.synthetic.main.fragment_school_parents.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.databinding.FragmentSchoolOnlineBinding
import ru.hvost.news.presentation.adapters.recycler.SchoolOnlineInfoAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolOnlineMaterialsAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineCourseActiveFragment:Fragment() {

    private lateinit var binding: FragmentSchoolOnlineBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController
    private val materialsAdapter = SchoolOnlineMaterialsAdapter()
    private val infoAdapter = SchoolOnlineInfoAdapter()
    private var schoolId:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        materialsAdapter.onClickLesson = object :SchoolOnlineMaterialsAdapter.OnClickLesson{
            override fun onClick() {
                navC.navigate(R.id.action_onlineCourseActiveFragment_to_onlineLessonFragment)
            }
        }
        setObservers(this)
        id?.run {
            schoolId = this
            schoolVM.getOnlineLessons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==",
                this)
            val schoolsResponse = schoolVM.onlineSchools.value
            schoolsResponse?.run {
                val schools = this.schools
                schoolId?.run {
                    for (i in schools.indices){
                        val idSchool = schools[i].id.toString()
                        if(idSchool==this){
                            val school = schools[i]
                            binding.textViewTitle.text = school.title
                            binding.textViewRank.text = school.userRank
                            Glide.with(requireContext()).load(APIService.baseUrl + school.image)
                                .placeholder(R.drawable.not_found).centerCrop()
                                .into(binding.imageViewLogo)
                            return@run
                        }
                    }
                }
            }
        }
        setSystemUiVisibility()
        setListeners()

        // for test
        val literatures = arrayListOf<OnlineSchoolsResponse.Literature>()
        for(i in 0 .. 10){
            val literature = OnlineSchoolsResponse.Literature("Нет апи","Нет апи", "Нет апи")
            literatures.add(literature)
        }
        materialsAdapter.setLiterature(literatures)
    }

    fun setObservers(owner:LifecycleOwner){
        schoolVM.onlineSchools.observe(owner, Observer {

        })

        schoolVM.onlineLessons.observe(owner, Observer {
            materialsAdapter.setLessons(it.lessons)
        })
    }

    private fun setListeners (){
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
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun setSystemUiVisibility (){
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}