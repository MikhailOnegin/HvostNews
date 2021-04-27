package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.models.CitiesOffline.CityOffline
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.getValue
import java.lang.Exception

class SchoolParentsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var citiesEvent: DefaultNetworkEventObserver
    private lateinit var navCSchoolParents: NavController
    private var fromDestination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        binding.toolbar2.background.level = 1
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCSchoolParents = requireActivity().findNavController(R.id.fragmentContainerSchoolParents)
        initializedAdapters()
        schoolVM.getSeminarsCities()
        initializeEvents()
        setObservers(this)
        fromDestination = findNavController().currentBackStackEntry?.savedStateHandle?.get("fromDestination")
        setListeners()
        setTabsSelected(fromDestination)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineCitiesEvent.observe(owner, citiesEvent)
        schoolVM.offlineCities.observe(owner, {
            schoolVM.offlineCities.value?.run {
                setCitiesToAdapter(this.cities)
            }
        })
    }



    @Suppress("UNCHECKED_CAST")
    private fun initializeEvents() {
        citiesEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.offlineCities.value?.run {
                 setCitiesToAdapter(this.cities)
                }
            }
        )
    }

    private fun initializedAdapters() {
        binding.spinnerOfflineSeminars.adapter =
            SpinnerAdapter(requireContext(), "", arrayListOf(), CityOffline::name)
        binding.spinnerOnlineSchools.adapter =
            SpinnerAdapter(
                requireContext(),
                "",
                resources.getStringArray(R.array.spinner_online_seminars).toCollection(ArrayList()),
                String::getValue
            )
    }

    private fun setListeners() {
        binding.constraintOnlineSchools.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.constraintOfflineSeminars.isSelected = false
                binding.constraintSpinnerOfflineSeminars.visibility = View.GONE
                binding.constraintSpinnerOnlineSchools.visibility = View.VISIBLE
                try {
                    navCSchoolParents.navigate(R.id.action_seminarsFragment_to_schoolsFragment)
                }
                catch (e:Exception){
                }
            }
        }
        binding.constraintOfflineSeminars.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.constraintOnlineSchools.isSelected = false
                binding.constraintSpinnerOfflineSeminars.visibility = View.VISIBLE
                binding.constraintSpinnerOnlineSchools.visibility = View.GONE
                try {
                    navCSchoolParents.navigate(R.id.action_schoolsFragment_to_seminarsFragment)
                }
                catch (e:Exception){
                }
            }
        }
        binding.switchFilter.setOnCheckedChangeListener { _, b ->
            schoolVM.showFinishedSeminars.value = b
        }
        binding.spinnerOfflineSeminars.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                @Suppress("UNCHECKED_CAST")
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).getItem(p2)
                        ?.let { cityOffline ->
                            val cityId = cityOffline.cityId
                            App.getInstance().userToken?.run {
                                schoolVM.getSeminars(cityId, this)
                                schoolVM.currentCity.value = cityOffline.cityId
                            }
                        }
                }
            }
        binding.spinnerOnlineSchools.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                @Suppress("UNCHECKED_CAST")
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    (binding.spinnerOnlineSchools.adapter as SpinnerAdapter<String>).getItem(p2)
                        ?.run {
                            schoolVM.filterSchools.value = this
                        }
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setCitiesToAdapter(citiesOffline: List<CityOffline>) {
        val adapter =
            (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>)
        adapter.clear()
        (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).add(
            CityOffline("all", getString(R.string.any_city))
        )
        (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).addAll(
            citiesOffline
        )
        (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).getItem(
            0
        )?.let { cityOffline ->
            val cityId = cityOffline.cityId
            App.getInstance().userToken?.run {
                schoolVM.getSeminars(cityId, this)
                schoolVM.currentCity.value = cityOffline.cityId
            }
        }
    }

    private fun setTabsSelected(destination: String?) {
        if (destination != null) {
            if (fromDestination == "school") {
                binding.constraintOnlineSchools.isSelected = true
                binding.constraintOfflineSeminars.isSelected = false
                binding.constraintSpinnerOnlineSchools.visibility = View.VISIBLE
                binding.constraintSpinnerOfflineSeminars.visibility = View.GONE
            }
            else if (fromDestination == "seminar") {
                binding.constraintOfflineSeminars.isSelected = true
                binding.constraintOnlineSchools.isSelected = false
                binding.constraintSpinnerOnlineSchools.visibility = View.GONE
                binding.constraintSpinnerOfflineSeminars.visibility = View.VISIBLE
            }
        } else {
            binding.constraintOnlineSchools.isSelected = true
            binding.constraintOfflineSeminars.isSelected = false
            binding.constraintSpinnerOnlineSchools.visibility = View.VISIBLE
            binding.constraintSpinnerOfflineSeminars.visibility = View.GONE
        }
    }
}
