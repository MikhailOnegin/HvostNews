package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_school_parents.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.models.CitiesOffline.CityOffline
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarsAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolsOnlineAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class ParentsSchoolFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private var onlineSchoolsAdapter = SchoolsOnlineAdapter()
    private var offlineSeminarsAdapter = OfflineSeminarsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        binding.recyclerView.adapter = onlineSchoolsAdapter
        App.getInstance().userToken?.run {
            schoolVM.getOnlineSchools(this)
        }
        schoolVM.getOfflineCities()
        binding.spinner.setSelection(0, false)
        binding.spinner.adapter =
            SpinnerAdapter(requireContext(), "", arrayListOf(), CityOffline::name)
        onlineSchoolsAdapter.clickSchool = object : SchoolsOnlineAdapter.ClickSchool {

            override fun onClick(schoolId: String) {
                val bundle = Bundle()
                bundle.putString("schoolId", schoolId)
                findNavController().navigate(
                    R.id.action_parentSchoolFragment_to_onlineCourseActiveFragment,
                    bundle
                )
            }
        }
        offlineSeminarsAdapter.onClickLesson =
            object : OfflineSeminarsAdapter.OnClickOfflineLesson {
                override fun onClick(lessonId: String) {
                    val bundle = Bundle()
                    bundle.putString("seminarId", lessonId)
                    findNavController().navigate(
                        R.id.action_parentSchoolFragment_to_offline_event_fragment,
                        bundle
                    )
                }
            }
        setObservers(this)
        setListeners()
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineSchoolsEvent.observe(owner, {
            onlineNetworkEvent(it)
        })

        schoolVM.offlineCitiesEvent.observe(owner, {
            citiesEvent(it)
        })

        schoolVM.offlineSeminarsEvent.observe(owner, {
            offlineSeminarsEvent(it)
        })
    }

    private val onlineNetworkEvent = { event: NetworkEvent<State> ->
        when (event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                schoolVM.onlineSchools.value?.onlineSchools?.run {
                    onlineSchoolsAdapter.setSchools(this)
                }
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
            }
            State.LOADING -> { }
            else -> { }
        }
    }

    private val offlineSeminarsEvent = { event: NetworkEvent<State> ->
        when (event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                schoolVM.offlineSeminars.value?.seminars?.run {
                    offlineSeminarsAdapter.setSeminars(this)
                }
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
            }
            State.LOADING -> { }
            else -> { }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val citiesEvent = { event: NetworkEvent<State> ->
        when (event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                schoolVM.offlineCities.value?.run {
                    val adapter = (binding.spinner.adapter as SpinnerAdapter<CityOffline>)
                    adapter.clear()
                    (binding.spinner.adapter as SpinnerAdapter<CityOffline>).addAll(this.cities)
                    (binding.spinner.adapter as SpinnerAdapter<CityOffline>).getItem(0)?.run {
                        val cityId = this.cityId
                        App.getInstance().userToken?.run {
                            schoolVM.getOfflineSeminars(cityId, this)
                        }
                    }
                }
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
            }
            State.LOADING -> { }
            else -> { }
        }
    }

    private fun setListeners() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val colorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)

        binding.constraintOnlineSchools.isSelected = true
        binding.onlineSchool.setTextColor(colorWhite)

        binding.constraintOnlineSchools.setOnClickListener {
            it.isSelected = true
            constraint_offline_seminars.isSelected = false
            binding.recyclerView.adapter = onlineSchoolsAdapter
            binding.onlineSchool.setTextColor(colorWhite)
            binding.offlineSeminars.setTextColor(colorPrimary)
            binding.constraintSpinner.visibility = View.GONE
        }
        binding.constraintOfflineSeminars.setOnClickListener {
            it.isSelected = true
            constraint_onlineSchools.isSelected = false
            binding.recyclerView.adapter = offlineSeminarsAdapter
            binding.offlineSeminars.setTextColor(colorWhite)
            binding.onlineSchool.setTextColor(colorPrimary)
            binding.constraintSpinner.visibility = View.VISIBLE
        }
        binding.switchFilter.setOnCheckedChangeListener { _, b ->
            if (binding.recyclerView.adapter is OfflineSeminarsAdapter) {
                offlineSeminarsAdapter.filter(b)
            }
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            @Suppress("UNCHECKED_CAST")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (binding.spinner.adapter as SpinnerAdapter<CityOffline>).getItem(p2)
                    ?.run {
                        val cityId = this.cityId
                        App.getInstance().userToken?.run {
                            schoolVM.getOfflineSeminars(cityId, this)
                        }
                    }
            }
        }
    }
}
