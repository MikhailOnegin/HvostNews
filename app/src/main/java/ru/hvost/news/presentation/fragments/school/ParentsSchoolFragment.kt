package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
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
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class ParentsSchoolFragment : Fragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var onlineSchoolsAdapter:SchoolsOnlineAdapter
    private lateinit var offlineSeminarsAdapter :OfflineSeminarsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        onlineSchoolsAdapter = SchoolsOnlineAdapter()
        offlineSeminarsAdapter = OfflineSeminarsAdapter()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        setSystemUiVisibility()
        setObservers(this)
        setListeners()
        binding.recyclerView.adapter = onlineSchoolsAdapter
        schoolVM.getOnlineSchools("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")

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
        offlineSeminarsAdapter.onClickLesson = object : OfflineSeminarsAdapter.OnClickOfflineLesson{
            override fun onClick(lessonId: String) {
                val bundle = Bundle()
                bundle.putString("seminarId", lessonId)
                findNavController().navigate(R.id.action_parentSchoolFragment_to_offline_event_fragment, bundle)
            }
        }
        setSystemUiVisibility()
        setObservers(this)
        setListeners()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner: LifecycleOwner) {

        schoolVM.onlineSchools.observe(owner, Observer {
            onlineSchoolsAdapter.setSchools(it.onlineSchools)
        })

        schoolVM.offlineCities.observe(owner, Observer {
            val adapter = (binding.spinner.adapter as SpinnerAdapter<CityOffline>)
            adapter.clear()
            (binding.spinner.adapter as SpinnerAdapter<CityOffline>).addAll(it.cities)
            (binding.spinner.adapter as SpinnerAdapter<CityOffline>).getItem(0)?.run {
                val cityId = this.cityId
                App.getInstance().userToken?.run {
                    schoolVM.getOfflineSeminars(cityId, this)
                }
            }
        })

        schoolVM.offlineSeminars.observe(owner, Observer {
            offlineSeminarsAdapter.setSeminars(it.seminars)
        })
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
        binding.switchFilter.setOnCheckedChangeListener { compoundButton, b ->
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

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}
