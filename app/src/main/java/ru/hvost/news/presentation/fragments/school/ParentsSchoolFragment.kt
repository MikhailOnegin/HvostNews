package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolParentsBinding
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarsAdapter
import ru.hvost.news.presentation.adapters.recycler.SchoolsOnlineAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.getValue

class ParentsSchoolFragment:Fragment() {

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
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        schoolVM.getOnlineSchools("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        schoolVM.getOfflineCities()
        setObservers(this)
        setListeners()
        schoolVM.getOfflineSeminars("5")
        binding.recyclerView.adapter = onlineSchoolsAdapter

        // for Test
        val cities = arrayListOf("1","2")
        val spinnerAdapter = SpinnerAdapter(requireContext(), "", cities, String::getValue)
        offlineLessonsAdapter.setSpinnerAdapter(spinnerAdapter)
    }

    private fun setObservers(owner:LifecycleOwner){

        schoolVM.onlineSchools.observe(owner, Observer {
            onlineSchoolsAdapter.setSchools(it.schools)
        })

        schoolVM.offlineCities.observe(owner, Observer {
            Log.i("eeee", "cities size: ${it.cities.size}")
            val cities = arrayListOf<String>()
            cities.add(getString(R.string.all))
            for(i in 0 .. it.cities.size ){
                val city = it.cities[i]
                cities.add(city.name)
            }
            val spinnerAdapter = SpinnerAdapter(requireContext(), "", cities, String::getValue)
                offlineLessonsAdapter.setSpinnerAdapter(spinnerAdapter)

        })

        schoolVM.offlineSeminars.observe(owner, Observer {
            Log.i("eeee", "Seminars size: ${it.seminars.size}")
            offlineLessonsAdapter.setSeminars(it.seminars)
        })
    }

    private fun setListeners(){
        binding.constraintOnlineScools.setOnClickListener {
            binding.recyclerView.adapter = onlineSchoolsAdapter
        }

        binding.constraintOfflineSeminars.setOnClickListener {
            binding.recyclerView.adapter = offlineLessonsAdapter
        }
    }

}