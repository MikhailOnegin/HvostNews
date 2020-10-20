package ru.hvost.news.presentation.fragments.school.parents

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.FragmentSchoolsBinding
import ru.hvost.news.models.OnlineSchool
import ru.hvost.news.presentation.adapters.recycler.SchoolsAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class SchoolsFragment: Fragment() {

    private lateinit var binding: FragmentSchoolsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val adapter = SchoolsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        navC = findNavController()
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val items = arrayOf("Все семинары", "Ваши семинары")
        binding.spinnerSeminars.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
        binding.recyclerViewSchools.adapter = adapter
        binding.recyclerViewSchools.layoutManager = layoutManager
        adapter.clickSchool = object :SchoolsAdapter.ClickSchool{
            override fun onClick(school: OnlineSchool.School) {
                navC.navigate(ru.hvost.news.R.id.action_parentSchoolFragment_to_onlineCourseActiveFragment)
            }
        }
        schoolVM.getOnlineSchools("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        setObservers(this)
    }
    private fun setObservers(owner:LifecycleOwner){
        schoolVM.onlineSchools.observe(owner, Observer {
            val schools = it.schools
            adapter.setSchools(schools)
        })
    }

}