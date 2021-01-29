package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolMaterialsBinding
import ru.hvost.news.presentation.adapters.recycler.SchoolMaterialsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class SchoolMaterialsFragment: BaseFragment() {

    private lateinit var binding: FragmentSchoolMaterialsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var lessonsEvent: DefaultNetworkEventObserver
    private lateinit var navCMain: NavController
    private lateinit var materialsAdapter: SchoolMaterialsAdapter
    private var schoolId: Long? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolMaterialsBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        initializedAdapters()
        binding.recyclerMaterials.adapter = materialsAdapter
        initializedEvents()
        setObservers(this)


    }

    private fun initializedEvents() {
        lessonsEvent = DefaultNetworkEventObserver(
                anchorView = binding.root,
                doOnSuccess = {
                    schoolVM.onlineLessons.value?.lessons?.let { lessons ->
                        schoolVM.onlineSchools.value?.onlineSchools?.let { schools ->
                            schoolVM.schoolOnlineId.value?.let { schoolId ->
                                for (i in schools.indices) {
                                    val school = schools[i]
                                    if (school.id == schoolId) {
                                        materialsAdapter.setValue(lessons, school)
                                    }
                                }
                            }
                        }
                    }
                }
        )
    }
    private fun initializedAdapters(){
        materialsAdapter = SchoolMaterialsAdapter(
                onClickLessonActive = {
                    val bundle = Bundle()
                    bundle.putString("schoolId", this.schoolId.toString())
                    bundle.putString("lessonId", it)
                    navCMain.navigate(
                            R.id.action_schoolFragment_to_lessonActiveFragment,
                            bundle
                    )
                },
                onClickLessonFinished = {
                    val bundle = Bundle()
                    bundle.putString("schoolId", this.schoolId.toString())
                    bundle.putString("lessonId", it)
                    navCMain.navigate(
                            R.id.action_schoolFragment_to_lessonFinishedFragment,
                            bundle
                    )
                }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineLessonsEvent.observe(owner, lessonsEvent)
        schoolVM.schoolOnlineId.observe(owner, {schoolId ->
            schoolVM.onlineSchools.value?.let {
                for(i in it.onlineSchools.indices){
                    val school = it.onlineSchools[i]
                    if(school.id == schoolId) {
                        App.getInstance().userToken?.run {
                            schoolVM.getSchoolLessons(this, school.id.toString())
                        }
                        this.schoolId = school.id
                        App.getInstance().userToken?.let { userToken ->
                            schoolVM.getSchoolLessons(userToken, school.id.toString())
                        }
                        return@observe
                    }
                }
            }
        })
    }
}