package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_school_parents.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.models.CitiesOffline
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarsAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolsOnlineAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getValue
import java.lang.invoke.ConstantCallSite

class ParentsSchoolFragment : Fragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private val onlineSchoolsAdapter = SchoolsOnlineAdapter()
    private val offlineLessonsAdapter = OfflineSeminarsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        binding.recyclerView.adapter = onlineSchoolsAdapter
        schoolVM.getOnlineSchools("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        schoolVM.getOfflineCities()
        binding.spinner.setSelection(0,false)
        binding.spinner.adapter = SpinnerAdapter(requireContext(), "", arrayListOf(), CitiesOffline.CityOffline::name)
        onlineSchoolsAdapter.clickSchool = object : SchoolsOnlineAdapter.ClickSchool{

            override fun onClick(schoolId: String) {
                val bundle = Bundle()
                bundle.putString("schoolId", schoolId)
                findNavController().navigate(R.id.action_parentSchoolFragment_to_onlineCourseActiveFragment, bundle)
            }
        }
        setSystemUiVisibility()
        setObservers(this)
        setListeners()
    }

    private fun setObservers(owner: LifecycleOwner) {

        schoolVM.onlineSchools.observe(owner, Observer {
            onlineSchoolsAdapter.setSchools(it.schools)
        })

        schoolVM.offlineCities.observe(owner, Observer {
            Log.i("eeee", "getOfflineCities() size : ${it.cities.size}")
            (binding.spinner.adapter as SpinnerAdapter<CitiesOffline.CityOffline>).addAll(it.cities)
            (binding.spinner.adapter as SpinnerAdapter<CitiesOffline.CityOffline>).getItem(0)?.run {
                schoolVM.getOfflineSeminars(this.cityId)
            }
        })

        schoolVM.offlineSeminars.observe(owner, Observer {
            Log.i("eeee", "Seminars size: ${it.seminars.size}")
            offlineLessonsAdapter.setSeminars(it.seminars)
        })
    }

    private fun setListeners() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val colorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)
        binding.constraintOnlineScools.isSelected = true
        binding.onlineSchool.setTextColor(colorWhite)

        binding.constraintOnlineScools.setOnClickListener {
            it.isSelected = true
            constraint_offline_seminars.isSelected = false
            binding.recyclerView.adapter = onlineSchoolsAdapter
            binding.onlineSchool.setTextColor(colorWhite)
            binding.offlineSeminars.setTextColor(colorPrimary)
            binding.constraintSpinner.layoutParams = ConstraintLayout.LayoutParams(0, 0)
        }
        binding.constraintOfflineSeminars.setOnClickListener {
            it.isSelected = true
            constraint_onlineScools.isSelected = false
            binding.recyclerView.adapter = offlineLessonsAdapter
            binding.offlineSeminars.setTextColor(colorWhite)
            binding.onlineSchool.setTextColor(colorPrimary)
            binding.constraintSpinner.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
        binding.switchFilter.setOnCheckedChangeListener { compoundButton, b ->
            if (binding.recyclerView.adapter is OfflineSeminarsAdapter) {
                offlineLessonsAdapter.filter(b)
            }
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (binding.spinner.adapter as SpinnerAdapter<CitiesOffline.CityOffline>).getItem(p2)
                    ?.run {
                        schoolVM.getOfflineSeminars(this.cityId)
                    }
            }
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun setSystemUiVisibility (){
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}
