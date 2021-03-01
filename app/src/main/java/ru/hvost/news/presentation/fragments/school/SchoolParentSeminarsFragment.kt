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
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentSeminarsBinding
import ru.hvost.news.presentation.adapters.recycler.SeminarsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class SchoolParentSeminarsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentSeminarsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var seminarsAdapter: SeminarsAdapter
    private lateinit var navMain: NavController
    private lateinit var seminarsEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentSeminarsBinding.inflate(inflater, container, false)
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        initializedAdapters()
        initializedEvents()
        binding.recyclerSeminars.adapter = seminarsAdapter
        setObservers(this)
        navMain = requireActivity().findNavController(R.id.nav_host_fragment)
        binding.swipeRefresh.isRefreshing = true
    }

    private fun initializedEvents() {
        seminarsEvent = DefaultNetworkEventObserver(
            anchorView  = binding.root,
            doOnLoading = { binding.swipeRefresh.isRefreshing = true },
            doOnSuccess = { binding.swipeRefresh.isRefreshing = false },
            doOnError   = { binding.swipeRefresh.isRefreshing = false },
            doOnFailure = { binding.swipeRefresh.isRefreshing = false }
        )
    }

    private fun initializedAdapters() {
        seminarsAdapter = SeminarsAdapter(
            schoolVM,
            clickSeminar = {
                schoolVM.seminarId.value = it
                navMain.navigate(
                    R.id.action_parentSchoolFragment_to_seminar_fragment
                )
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, { seminarsResponse ->
            seminarsAdapter.setSeminars(seminarsResponse.seminars)
            schoolVM.filterShowFinished.value?.let {
                seminarsAdapter.filter(it)
            }
        })
        schoolVM.filterShowFinished.observe(owner,
            {
                seminarsAdapter.filter(it)
            })
        schoolVM.adapterSeminarsSize.observe(owner, {
            if (it > 0) {
                binding.scrollViewEmpty.visibility = View.GONE
                binding.recyclerSeminars.visibility = View.VISIBLE
            }
            else {
                binding.scrollViewEmpty.visibility = View.VISIBLE
                binding.recyclerSeminars.visibility = View.GONE
            }
        })

    }
}