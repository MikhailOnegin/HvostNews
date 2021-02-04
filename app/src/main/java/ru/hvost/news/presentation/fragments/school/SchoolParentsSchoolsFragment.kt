package ru.hvost.news.presentation.fragments.school

import android.animation.ObjectAnimator
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
import ru.hvost.news.presentation.adapters.recycler.SchoolsListAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.OneTimeEvent

class SchoolParentsSchoolsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsSchoolsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var schoolsAdapter: SchoolsListAdapter
    private lateinit var navCMain: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsSchoolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        initializedAdapters()
        binding.recyclerSchools.adapter = schoolsAdapter
        setObservers(this)
    }

    private fun initializedAdapters() {

        schoolsAdapter = SchoolsListAdapter(
            clickSchool = {
                schoolVM.schoolOnlineId.value = it
                val bundle = Bundle()
                bundle.putString("schoolId", it.toString())
                navCMain.navigate(
                    R.id.action_parentSchoolFragment_to_schoolFragment,
                    bundle
                )
            },
            schoolVM = schoolVM
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineSchools.observe(owner, { schoolsResponse ->
            schoolsAdapter.submitList(schoolsResponse.onlineSchools)
            schoolVM.filterSchools.value?.let {
                schoolsAdapter.filterYourSchools(it)
            }
        })
        schoolVM.filterSchools.observe(owner, {
            schoolsAdapter.filterYourSchools(it)
        })
        schoolVM.recyclerSchoolsReadyEvent.observe(owner) { onRecyclerSchoolsReadyEvent(it) }
        schoolVM.adapterSchoolsSize.observe(owner, {
            if (it > 0) binding.recyclerSchools.visibility = View.VISIBLE
            else binding.recyclerSchools.visibility = View.GONE
        }
        )
    }

    private fun onRecyclerSchoolsReadyEvent(event: OneTimeEvent?) {
        event?.getEventIfNotHandled()?.run {
            binding.progress.visibility = View.GONE
            ObjectAnimator.ofFloat(
                binding.recyclerSchools,
                "alpha",
                0f, 1f
            ).apply {
                duration = 300L
            }.start()
        }
    }
}