package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentSeminarsBinding
import ru.hvost.news.models.CitiesOffline
import ru.hvost.news.presentation.adapters.recycler.SeminarsAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getValue

class SchoolParentSeminarsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentSeminarsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var seminarsAdapter: SeminarsAdapter
    private lateinit var navMain: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentSeminarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        initializedAdapters()
        binding.recyclerSeminars.adapter = seminarsAdapter
        setObservers(this)
        navMain = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun initializedAdapters() {
        seminarsAdapter = SeminarsAdapter(
            clickSeminar = {
                schoolVM.seminarId.value = it
                navMain.navigate(
                    R.id.action_parentSchoolFragment_to_seminar_fragment,
                )
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, { seminarsResponse ->
            if (seminarsResponse.seminars.isEmpty()) binding.scrollViewEmpty.visibility =
                View.VISIBLE
            else binding.scrollViewEmpty.visibility = View.GONE
            seminarsAdapter.setSeminars(seminarsResponse.seminars)
            schoolVM.filterShowFinished.value?.let {
                seminarsAdapter.filter(it)
            }
        })
        schoolVM.filterShowFinished.observe(owner,
            {
                seminarsAdapter.filter(it)
            })
    }
}