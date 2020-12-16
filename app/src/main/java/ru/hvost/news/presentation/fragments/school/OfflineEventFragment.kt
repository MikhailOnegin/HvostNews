package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_school_offline_event.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOfflineEventBinding
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarInfoAdapter
import ru.hvost.news.presentation.adapters.recycler.OfflineSeminarScheduleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OfflineEventFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOfflineEventBinding
    private lateinit var schoolVM: SchoolViewModel
    private var seminarId: String? = null
    private var infoAdapter = OfflineSeminarInfoAdapter()
    private var scheduleAdapter = OfflineSeminarScheduleAdapter()
    private var active: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolOfflineEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        setListeners()
        seminarId = arguments?.getString("seminarId")
        setObservers(this)
        //binding.recyclerView.adapter = infoAdapter
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, {
            seminarId?.run {
                for (i in it.seminars.indices) {
                    val seminar = it.seminars[i]
                    infoAdapter.setSeminar(seminar)
                    scheduleAdapter.setSeminar(seminar)
                    if (seminar.id.toString() == seminarId) {
                        binding.textViewTitle.text = seminar.title
                        if (seminar.isFinished) {
                            binding.textViewLessonStatus.text = getString(R.string.completed)
                            binding.textViewLessonStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.red ))
                            binding.buttonParticipate.text = "Подписаться"
                            binding.buttonParticipate.setOnClickListener {
                                binding.buttonParticipate.text = "Вы подписаны на уведомления"
                                binding.buttonParticipate.setTextColor(ContextCompat.getColor(requireContext(),R.color.gray1 ))
                                binding.buttonParticipate.background.setTint(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.gray4
                                    )
                                )
                            }
                        }
                        else {
                            binding.textViewLessonStatus.text = getString(R.string.active)
                            binding.textViewLessonStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                            binding.buttonParticipate.visibility = View.VISIBLE
                            if (seminar.participate) {
                                binding.buttonParticipate.text = getString(R.string.you_participate)
                                binding.buttonParticipate.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray1))
                                binding.buttonParticipate.background.setTint(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.gray4
                                    )
                                )
                                binding.buttonParticipate.isEnabled = false
                            } else {
                                binding.buttonParticipate.text = getString(R.string.participate)
                                binding.buttonParticipate.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                                binding.buttonParticipate.background.setTint(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPrimary
                                    )
                                )
                                binding.buttonParticipate.isEnabled = true
                                binding.buttonParticipate.setOnClickListener {
                                    Toast.makeText(requireContext(), "Записываем", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        binding.textViewCity.text = seminar.city
                        binding.textViewDate.text = seminar.date
                        binding.textViewSponsor.text = seminar.sponsor
                        active = seminar.participate
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
           // binding.recyclerView.adapter = infoAdapter
            binding.seminarInfo.setTextColor(colorWhite)
            binding.seminarSchedule.setTextColor(colorPrimary)
        }
        binding.constraintSeminarSchedule.setOnClickListener {
            it.isSelected = true
            constraint_seminar_info.isSelected = false
           // binding.recyclerView.adapter = scheduleAdapter
            binding.seminarSchedule.setTextColor(colorWhite)
            binding.seminarInfo.setTextColor(colorPrimary)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}