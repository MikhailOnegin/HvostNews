package ru.hvost.news.presentation.fragments.school


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsSchoolsBinding
import ru.hvost.news.presentation.adapters.recycler.SchoolsListAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class SchoolParentsSchoolsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsSchoolsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var schoolsAdapter: SchoolsListAdapter
    private lateinit var navCMain: NavController
    private lateinit var schoolsEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsSchoolsBinding.inflate(inflater, container, false)
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        initializedAdapters()
        initializedEvents()
        binding.recyclerSchools.adapter = schoolsAdapter
        setObservers(this)
        setListeners()
        getData()
    }

    private fun getData() {
        schoolVM.onlineSchools.value?.let {
            schoolsAdapter.submitList(it.onlineSchools)
        }
        App.getInstance().userToken?.let {
            schoolVM.getSchools(it)
        }
    }


    private fun initializedEvents() {
        schoolsEvent = DefaultNetworkEventObserver(
                anchorView =  binding.root,
                doOnSuccess = {
                    schoolVM.onlineSchools.value?.let {
                        schoolsAdapter.submitList(it.onlineSchools)
                    }
                }
        )
    }

    private fun setListeners() {
        binding.swipeRefresh.setOnRefreshListener{
            App.getInstance().userToken?.let {
                schoolVM.getSchools(it)
            }
        }
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
        schoolVM.onlineSchoolsEvent.observe(owner, schoolsEvent)
        schoolVM.filterSchools.observe(owner, {
            schoolsAdapter.filterYourSchools(it, requireContext())
        })
        schoolVM.recyclerSchoolsReadyEvent.observe(owner) { onRecyclerSchoolsReadyEvent(it) }
        schoolVM.adapterSchoolsSize.observe(owner, {
            if (it > 0) {
                binding.root.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                binding.scrollViewEmpty.visibility = View.GONE
                binding.recyclerSchools.visibility = View.VISIBLE
            }
            else {
                binding.root.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                binding.scrollViewEmpty.visibility = View.VISIBLE
                binding.recyclerSchools.visibility = View.GONE
            }
        }
        )
    }

    private fun onRecyclerSchoolsReadyEvent(event: OneTimeEvent?) {
        event?.getEventIfNotHandled()?.run {
            binding.swipeRefresh.isRefreshing = false
    }
    }
}