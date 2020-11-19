package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_school_parents.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarsAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolsOnlineAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getValue

class ParentsSchoolFragment:Fragment() {

    private lateinit var binding: FragmentSchoolParentsBinding
    private lateinit var schoolVM: SchoolViewModel
    private val onlineSchoolsAdapter = SchoolsOnlineAdapter()
    private val offlineLessonsAdapter = OfflineSeminarsAdapter()
    private val citiesMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolParentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = onlineSchoolsAdapter
        schoolVM.getOnlineSchools("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        schoolVM.getOfflineCities()
        offlineLessonsAdapter.spinnerListener = object:OfflineSeminarsAdapter.SpinnerSelected{
            override fun onSelected(position: Int) {
                val city = offlineLessonsAdapter.spinnerAdapter?.getItem(position)
                city?.run {
                    val cityId = citiesMap[this]
                    cityId?.run {
                        schoolVM.getOfflineSeminars(this)
                    }
                }
            }
        }
        val itemsSpinnerOnline = arrayListOf("Все семинары", "Ваши семинары")
        onlineSchoolsAdapter.spinnerAdapter = SpinnerAdapter(requireContext(), "", itemsSpinnerOnline, String::getValue)
        binding.constraintOnlineScools.isSelected = true
        binding.onlineSchool.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        setObservers(this)
        setListeners()
    }

    private fun setObservers(owner:LifecycleOwner){

        schoolVM.onlineSchools.observe(owner, Observer {
            onlineSchoolsAdapter.setSchools(it.schools)
        })

        schoolVM.offlineCities.observe(owner, Observer {
            val citiesList = arrayListOf<String>()
            for(element in it.cities){
                citiesList.add(element.name)
                citiesMap[element.name] = element.cityId
            }
            val spinnerAdapter = SpinnerAdapter(requireContext(), "", citiesList, String::getValue)
                offlineLessonsAdapter.spinnerAdapter = spinnerAdapter
            // need 0, 2 for test
            val cityId = citiesMap[citiesList[2]]
            cityId?.run {
                schoolVM.getOfflineSeminars(this)
            }

        })

        schoolVM.offlineSeminars.observe(owner, Observer {
            Log.i("eeee", "Seminars size: ${it.seminars.size}")
            offlineLessonsAdapter.setSeminars(it.seminars)
        })
    }

    private fun setListeners(){
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val colorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)
        binding.constraintOnlineScools.setOnClickListener {
            it.isSelected = true
            constraint_offline_seminars.isSelected = false
            binding.recyclerView.adapter = onlineSchoolsAdapter
            binding.onlineSchool.setTextColor(colorWhite)
            binding.offlineSeminars.setTextColor(colorPrimary)
        }

        binding.constraintOfflineSeminars.setOnClickListener {
            it.isSelected = true
            constraint_onlineScools.isSelected = false
            binding.recyclerView.adapter = offlineLessonsAdapter
            binding.offlineSeminars.setTextColor(colorWhite)
            binding.onlineSchool.setTextColor(colorPrimary)
        }
    }
}