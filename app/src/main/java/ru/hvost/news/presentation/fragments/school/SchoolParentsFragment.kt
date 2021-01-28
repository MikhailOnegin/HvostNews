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

class SchoolParentsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var citiesEvent: DefaultNetworkEventObserver
    private lateinit var navCSchoolParents:NavController
    private var fromDestination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        binding.toolbar2.background.level = 1
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCSchoolParents = requireActivity().findNavController(R.id.fragmentContainerSchoolParents)
        initializedAdapters()
        if ( schoolVM.onlineSchools.value == null) {
            App.getInstance().userToken?.run {
                schoolVM.getSchools(this)
            }
        }
        if (schoolVM.offlineSeminars.value == null) schoolVM.getSeminarsCities()
        initializeEvents()
        setObservers(this)
        setListeners()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fromDestination")
            ?.observe(viewLifecycleOwner) {
                fromDestination = it
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineCitiesEvent.observe(owner, citiesEvent)
        schoolVM.offlineCities.observe(owner, {
            schoolVM.offlineCities.value?.run {
                val adapter =
                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>)
                adapter.clear()
                (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).add(
                        CityOffline("all", "Любой город")
                )
                (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).addAll(
                    this.cities
                )
                (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).getItem(
                    0
                )?.run {
                    val cityId = this.cityId
                    App.getInstance().userToken?.run {
                        schoolVM.getSeminars(cityId, this)
                    }
                }
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeEvents() {
        citiesEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.offlineCities.value?.run {
                    val adapter =
                        (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>)
                    adapter.clear()

                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).add(
                        CityOffline("all", "Любой город")
                    )
                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).addAll(
                        this.cities
                    )
                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).getItem(
                        0
                    )?.run {
                        val cityId = this.cityId
                        App.getInstance().userToken?.run {
                            schoolVM.getSeminars(cityId, this)
                        }
                    }
                }
            }
        )
    }
    private fun initializedAdapters(){
        binding.spinnerOfflineSeminars.adapter =
                SpinnerAdapter(requireContext(), "", arrayListOf(), CityOffline::name)
        binding.spinnerOnlineSchools.adapter =
                SpinnerAdapter(
                        requireContext(),
                        "",
                        arrayListOf("Все семинары", "Ваши семинары"),
                        String::getValue
                )
    }

    private fun setListeners() {


        if(fromDestination != null) {
            if (fromDestination == "school"){
                binding.constraintOnlineSchools.isSelected = true
            }
            else if(fromDestination == "seminar"){
                binding.constraintOfflineSeminars.isSelected = true
            }
        }
        else binding.constraintOnlineSchools.isSelected = true
        binding.constraintOnlineSchools.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.constraintOfflineSeminars.isSelected = false
                navCSchoolParents.navigate(R.id.action_seminarsFragment_to_schoolsFragment)
                binding.constraintSpinnerOfflineSeminars.visibility = View.GONE
                binding.constraintSpinnerOnlineSchools.visibility = View.VISIBLE
            }
        }
        binding.constraintOfflineSeminars.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                binding.constraintOnlineSchools.isSelected = false
                navCSchoolParents.navigate(R.id.action_schoolsFragment_to_seminarsFragment)
                binding.constraintSpinnerOfflineSeminars.visibility = View.VISIBLE
                binding.constraintSpinnerOnlineSchools.visibility = View.GONE
            }
        }
        binding.switchFilter.setOnCheckedChangeListener { _, b ->
            schoolVM.filterShowFinished.value = b
        }

        binding.spinnerOfflineSeminars.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                @Suppress("UNCHECKED_CAST")
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    (binding.spinnerOfflineSeminars.adapter as SpinnerAdapter<CityOffline>).getItem(p2)
                        ?.run {
                            val cityId = this.cityId
                            App.getInstance().userToken?.run {
                                schoolVM.getSeminars(cityId, this)
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
}
