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
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCourseMaterialBinding
import ru.hvost.news.presentation.adapters.listview.LessonsListViewAdapter
import ru.hvost.news.presentation.adapters.recycler.LessonsOnlineAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class CourseMaterialsFragment:Fragment() {

    private lateinit var binding: FragmentCourseMaterialBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navC:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        navC = findNavController()
        setObservers(this)
        schoolVM.getOnlineLessons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ",
            "17174"
            )

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