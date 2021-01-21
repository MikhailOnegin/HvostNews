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
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.getValue

class SchoolParentsFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private var onlineSchoolsAdapter = SchoolsOnlineAdapter()
    private var offlineSeminarsAdapter = OfflineSeminarsAdapter()
    private lateinit var citiesEvent: DefaultNetworkEventObserver
    private lateinit var offlineSeminarsEvent: DefaultNetworkEventObserver
    private lateinit var onlineSchoolsEvent: DefaultNetworkEventObserver

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
        if ( schoolVM.onlineSchools.value == null) {
            App.getInstance().userToken?.run {
                schoolVM.getOnlineSchools(this)
            }
        }
        if (schoolVM.offlineSeminars.value == null) schoolVM.getOfflineCities()
        binding.spinnerOfflineSeminars.adapter =
            SpinnerAdapter(requireContext(), "", arrayListOf(), CityOffline::name)
        binding.spinnerOnlineSchools.adapter =
            SpinnerAdapter(
                requireContext(),
                "",
                arrayListOf("Все семинары", "Ваши семинары"),
                String::getValue
            )
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
                override fun onClick(lessonId: Int) {
                    schoolVM.seminarId.value = lessonId
                    findNavController().navigate(
                        R.id.action_parentSchoolFragment_to_offline_event_fragment,
                    )
                }
            }
        initializeEvents()
        setObservers(this)
        setListeners()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.onlineSchoolsEvent.observe(owner, onlineSchoolsEvent)

        schoolVM.offlineCitiesEvent.observe(owner, citiesEvent)

        schoolVM.offlineSeminarsEvent.observe(owner, offlineSeminarsEvent)
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
                        schoolVM.getOfflineSeminars(cityId, this)
                    }
                }
            }
        })
        schoolVM.offlineSeminars.observe(owner, {
            offlineSeminarsAdapter.setSeminars(it.seminars)
            if(it.seminars.isNotEmpty()) binding.textViewEmpty.visibility = View.GONE
            else binding.textViewEmpty.visibility = View.VISIBLE
        })
        schoolVM.onlineSchools.observe(owner, {
            onlineSchoolsAdapter.setSchools(it.onlineSchools)
            if(it.onlineSchools.isNotEmpty()) binding.textViewEmpty.visibility = View.GONE
            else binding.textViewEmpty.visibility = View.VISIBLE
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
                            schoolVM.getOfflineSeminars(cityId, this)
                        }
                    }
                }
            }
        )
        offlineSeminarsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.offlineSeminars.value?.seminars?.run {
                    offlineSeminarsAdapter.setSeminars(this)
                    offlineSeminarsAdapter.filter(binding.switchFilter.isChecked)
                    if(offlineSeminarsAdapter.lessons.isNotEmpty()) textViewEmpty.visibility = View.GONE
                    else textViewEmpty.visibility = View.VISIBLE
                }
            }
        )
        onlineSchoolsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                schoolVM.onlineSchools.value?.onlineSchools?.run {
                    onlineSchoolsAdapter.setSchools(this)
                    if(this.isNotEmpty()) binding.textViewEmpty.visibility = View.GONE
                    else binding.textViewEmpty.visibility = View.VISIBLE
                }
            }
        )
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
            binding.constraintSpinnerOfflineSeminars.visibility = View.GONE
            binding.constraintSpinnerOnlineSchools.visibility = View.VISIBLE
        }
        binding.constraintOfflineSeminars.setOnClickListener {
            it.isSelected = true
            constraint_onlineSchools.isSelected = false
            binding.recyclerView.adapter = offlineSeminarsAdapter
            binding.offlineSeminars.setTextColor(colorWhite)
            binding.onlineSchool.setTextColor(colorPrimary)
            binding.constraintSpinnerOfflineSeminars.visibility = View.VISIBLE
            binding.constraintSpinnerOnlineSchools.visibility = View.GONE
        }
        binding.switchFilter.setOnCheckedChangeListener { _, b ->
            offlineSeminarsAdapter.filter(b)
            if(offlineSeminarsAdapter.itemCount > 0) binding.textViewEmpty.visibility = View.GONE
            else binding.textViewEmpty.visibility = View.VISIBLE
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
                                schoolVM.getOfflineSeminars(cityId, this)
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
                            onlineSchoolsAdapter.filterYourSchools(this)
                        }
                }
            }
    }
}
