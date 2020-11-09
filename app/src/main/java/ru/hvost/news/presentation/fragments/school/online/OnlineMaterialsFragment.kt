package ru.hvost.news.presentation.fragments.school.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import okhttp3.internal.notify
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOnlineMaterialsBinding
import ru.hvost.news.presentation.adapters.listview.LessonsListViewAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OnlineMaterialsFragment:Fragment() {

    private lateinit var binding: FragmentSchoolOnlineMaterialsBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOnlineMaterialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        navC = findNavController()
        schoolVM.getOnlineLessons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ",
            "17174"
        )
        setObservers(this)
    }

    fun setObservers(owner: LifecycleOwner){
        schoolVM.onlineLessons.observe(owner, Observer { onlineLessons ->
            val adapter = LessonsListViewAdapter(requireContext(), R.layout.item_lesson_online, onlineLessons.lessons
            ) {
                val bundle = Bundle()
                bundle.putSerializable("lesson", it)
                navC.navigate(R.id.action_onlineCourseActiveFragment_to_onlineCourseLessonFragment)

            }
            binding.listView.adapter = adapter
        })
    }
}