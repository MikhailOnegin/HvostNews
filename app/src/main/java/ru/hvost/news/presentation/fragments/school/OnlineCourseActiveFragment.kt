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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.databinding.FragmentSchoolOnlineBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.adapters.recycler.OnlineSchoolAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineCourseActiveFragment:Fragment() {

    private lateinit var binding: FragmentSchoolOnlineBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController
    val adapter = OnlineSchoolAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOnlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        navC = findNavController()

        val school = arguments?.get("school")
        if(school is OnlineSchools.School){
            if(school.title.isNotBlank()) binding.textViewTitle.text = school.title
            if(school.userRank.isNotBlank()) binding.textViewRank.text = school.userRank
            schoolVM.getOnlineLessons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ" ,
                17174.toString())
        }

        setAdapters()
        setObservers(this)
    }

    private fun setAdapters(){
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        val literatures = mutableListOf<OnlineSchoolsResponse.Literature>()
        for(i in 0 .. 10){
            val literature = OnlineSchoolsResponse.Literature("Поездка за границу с притомцем",
            "Для щенка", "ыфвыф")
            literatures.add(literature)
        }
        adapter.setLiterature(literatures)

    }
    fun setObservers(owner:LifecycleOwner){
        schoolVM.onlineLessons.observe(owner, Observer {
            Log.i("eeee", "loadLessons")
            Log.i("eeee", "lessonSize: ${it.lessons.size}")
            adapter.setLessons(it.lessons)
        })
    }
}