package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsSchoolsBinding
import ru.hvost.news.presentation.adapters.recycler.SchoolsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class SchoolParentsSchoolsFragment: BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsSchoolsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var schoolsAdapter:SchoolsAdapter
    private lateinit var navCMain:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsSchoolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        initializedAdapters()
        binding.recyclerSchools.adapter = schoolsAdapter
        setObservers(this)
    }

    private fun initializedAdapters(){

        schoolsAdapter = SchoolsAdapter(
            clickSchool = {
                schoolVM.schoolOnlineId.value = it
                val bundle = Bundle()
                bundle.putString("schoolId", it.toString())
                navCMain.navigate(
                    R.id.action_parentSchoolFragment_to_schoolFragment,
                    bundle
                )
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineSchools.observe(owner, { schoolsResponse ->
            schoolsAdapter.setSchools(schoolsResponse.onlineSchools)
            schoolVM.filterSchools.value?.let {
                schoolsAdapter.filterYourSchools(it)
            }
        })
        schoolVM.filterSchools.observe(owner, {
            schoolsAdapter.filterYourSchools(it)
        })
    }
}