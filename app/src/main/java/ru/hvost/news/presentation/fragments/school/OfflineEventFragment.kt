package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_school_offline_event.*
import kotlinx.android.synthetic.main.fragment_school_parents.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOfflineEventBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OfflineEventFragment : Fragment() {

    private lateinit var binding: FragmentSchoolOfflineEventBinding
    private lateinit var schoolVM: SchoolViewModel
    private var seminarId:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolOfflineEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        setListeners()
        seminarId = arguments?.getString("seminarId")
        setObservers(this)
        setSystemUiVisibility()
    }

    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, Observer {
            seminarId?.run {
                Log.i("eeee", "seminarId $seminarId")
                for (i in it.seminars.indices){
                    val seminar = it.seminars[i]
                    if (seminar.id == seminarId){
                        binding.textViewTitle.text = seminar.title
                        if(seminar.isFinished){
                            binding.textViewLessonStatus.text = getString(R.string.comleted)
                            binding.textViewLessonStatus.setTextColor(resources.getColor(R.color.red))}
                        else {
                            binding.textViewLessonStatus.text =  getString(R.string.active)
                            binding.textViewLessonStatus.setTextColor(resources.getColor(R.color.colorPrimary))

                        }
                        binding.textViewCity.text = seminar.city
                        binding.textViewDate.text = seminar.date
                        binding.textViewSponsor.text = seminar.sponsor
                        if(seminar.participate){
                            binding.buttonParticipate.text = getString(R.string.you_prticipate)
                            binding.buttonParticipate.setTextColor(resources.getColor(android.R.color.white))
                            binding.buttonParticipate.background.setTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        }
                        else{
                            binding.buttonParticipate.text = getString(R.string.participate)
                            binding.buttonParticipate.setTextColor(resources.getColor(R.color.gray1))
                            binding.buttonParticipate.background.setTint(ContextCompat.getColor(requireContext(), R.color.gray4))
                        }

                    }

                }

            }
        })
    }

    private fun setListeners() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val colorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)
        binding.constraintSeminarInfo.isSelected = true
        binding.seminarInfo.setTextColor(colorWhite)
        binding.constraintSeminarInfo.isSelected = true
        binding.constraintSeminarInfo.setOnClickListener {
            it.isSelected = true
            constraint_seminar_schedule.isSelected = false
            //inding.recyclerView.adapter = onlineSchoolsAdapter
            binding.seminarInfo.setTextColor(colorWhite)
            binding.seminarSchedule.setTextColor(colorPrimary)
        }
        binding.constraintSeminarSchedule.setOnClickListener {
            it.isSelected = true
            constraint_seminar_info.isSelected = false
            //binding.recyclerView.adapter = offlineLessonsAdapter
            binding.seminarSchedule.setTextColor(colorWhite)
            binding.seminarInfo.setTextColor(colorPrimary)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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