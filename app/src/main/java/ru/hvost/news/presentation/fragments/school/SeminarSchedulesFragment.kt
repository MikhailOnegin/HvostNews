package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSeminarSchedulesBinding
import ru.hvost.news.databinding.LayoutSeminarScheduleBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.presentation.adapters.recycler.PartnersAdapter
import ru.hvost.news.presentation.adapters.recycler.VideoPastSeminarsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.dateFormat

class SeminarSchedulesFragment : BaseFragment() {

    private lateinit var binding: FragmentSeminarSchedulesBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navCMain: NavController
    private val adapterVideo = VideoPastSeminarsAdapter()
    private val adapterPartners = PartnersAdapter()
    private var seminar: OfflineSeminars.OfflineSeminar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeminarSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        binding.recyclerViewVideo.adapter = adapterVideo
        binding.recyclerViewSponsors.adapter = adapterPartners
        binding.recyclerViewSponsors.layoutManager = GridLayoutManager(requireContext(), 2)
        navCMain.previousBackStackEntry?.savedStateHandle?.set("fromDestination", "seminar")
        setObservers(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navCMain.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, onBackPressedCallback
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.petTypeName.observe(owner, {
            schoolVM.offlineSeminars.value?.let { offlineSeminars ->
                schoolVM.seminarId.value.let { seminarId ->
                    for (i in offlineSeminars.seminars.indices) {
                        val seminar = offlineSeminars.seminars[i]
                        if (seminar.id == seminarId) {
                            this.seminar = seminar
                            if (seminar.videos.isNotEmpty()) {
                                binding.constraintPastVideos.visibility = View.VISIBLE
                                adapterVideo.setVideos(seminar.videos)
                            }
                            if (seminar.partners.isNotEmpty()) {
                                binding.constraintPartners.visibility = View.VISIBLE
                                adapterPartners.setPartners(seminar.partners)
                            }
                            schoolVM.petTypeName.value?.let { petTypeName ->
                                for (q in seminar.petSchedules.indices) {
                                    val petSchedule = seminar.petSchedules[q]
                                    if (petSchedule.petTypeName == petTypeName) {
                                        val linearSchedules = binding.linearLayoutSchedules
                                        linearSchedules.removeAllViews()
                                        for (j in petSchedule.schedules.indices) {
                                            val schedule = petSchedule.schedules[j]
                                            val viewSchedule = LayoutSeminarScheduleBinding.inflate(
                                                LayoutInflater.from(requireContext()),
                                                linearSchedules,
                                                false
                                            )
                                            viewSchedule.textViewTitle.text = schedule.title
                                            viewSchedule.textViewTime.text = schedule.title
                                            viewSchedule.textViewDescription.text =
                                                schedule.description.parseAsHtml()
                                            viewSchedule.textViewDate.text =
                                                dateFormat(schedule.date)
                                            val time =
                                                "${schedule.timeStart} - ${schedule.timeFinish}"
                                            viewSchedule.textViewTime.text = time
                                            linearSchedules.addView(viewSchedule.root)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}