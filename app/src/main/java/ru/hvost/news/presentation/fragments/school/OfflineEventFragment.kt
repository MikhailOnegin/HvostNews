package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.layout_tab_item_seminar.view.*
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOfflineEventBinding
import ru.hvost.news.databinding.LayoutTabItemSeminarBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class OfflineEventFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOfflineEventBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var fm: FragmentManager
    private var seminarId: String? = null
    private var active: Boolean? = null
    private var tabsView = arrayListOf<ConstraintLayout>()

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
        initializedEvents()
        setObservers(this)
        fm = (activity as AppCompatActivity).supportFragmentManager
        binding.horizontalScrollView.isSmoothScrollingEnabled = false
        binding.horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
    }

    private fun initializedEvents() {
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, {
            seminarId?.run {
                val d = 2
                for (i in it.seminars.indices) {
                    val seminar = it.seminars[i]
                    if (seminar.id.toString() == seminarId) {

                        if (seminar.isFinished) {
                            binding.textViewLessonStatus.text = getString(R.string.completed)
                            binding.textViewLessonStatus.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                            binding.buttonParticipate.text = "Подписаться"
                            binding.buttonParticipate.setOnClickListener {
                                binding.buttonParticipate.text =
                                    "Вы подписаны на уведомления (апи не готово)"
                                binding.buttonParticipate.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.gray1
                                    )
                                )
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
                            binding.textViewLessonStatus.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            binding.buttonParticipate.visibility = View.VISIBLE
                            if (seminar.participate) {
                                binding.buttonParticipate.text = getString(R.string.you_participate)
                                binding.buttonParticipate.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.gray1
                                    )
                                )
                                binding.buttonParticipate.background.setTint(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.gray4
                                    )
                                )
                                binding.buttonParticipate.isEnabled = false
                            }
                            else {
                                binding.buttonParticipate.text = getString(R.string.participate)
                                binding.buttonParticipate.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        android.R.color.white
                                    )
                                )
                                binding.buttonParticipate.background.setTint(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPrimary
                                    )
                                )
                                binding.buttonParticipate.isEnabled = true
                                binding.buttonParticipate.setOnClickListener {
                                    seminarId?.run {
                                        val bundle = Bundle()
                                        bundle.putString("seminarId", this)
                                        findNavController().navigate(
                                            R.id.action_offlineEventFragment_to_registrationFragment,
                                            bundle
                                        )
                                    }

                                }
                            }
                        }
                        val linearTabs = binding.linearLayoutTabs
                        linearTabs.removeAllViews()
                        val viewTab = LayoutTabItemSeminarBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            linearTabs,
                            false
                        ).root
                        viewTab.textView_tab.text = getString(R.string.seminar_info)
                        viewTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.sex_selector_left)
                        viewTab.textView_tab.isSelected = true
                        viewTab.isSelected = true
                        tabsView.add(viewTab)
                        setListenerTab(viewTab, tabsView)
                        linearTabs.addView(viewTab)
                        for (q in seminar.petSchedules.indices){
                            val viewTab2 = LayoutTabItemSeminarBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                linearTabs,
                                false
                            ).root
                            val petTypeName = seminar.petSchedules[q].petTypeName
                            if (q == seminar.petSchedules.size - 1 ) viewTab2.background = ContextCompat.getDrawable(requireContext(), R.drawable.sex_selector_right)
                            else viewTab2.background = ContextCompat.getDrawable(requireContext(), R.drawable.sex_selector_middle)
                            val tabText = "${getString(R.string.schedule_for)} $petTypeName"
                            viewTab2.textView_tab.text = tabText
                            tabsView.add(viewTab2)
                            setListenerTab(viewTab2, tabsView)
                            linearTabs.addView(viewTab2)
                        }
                        binding.textViewTitle.text = seminar.title
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
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonParticipate.setOnClickListener {
            val bundle = Bundle()
            seminarId?.run {
                bundle.putString("schoolId", this)
            }
            findNavController().navigate(
                R.id.action_offlineEventFragment_to_registrationFragment,
                bundle
            )
        }
    }

   private fun setListenerTab(view:View, list:List<View>){
        view.setOnClickListener {
            for( i in list.indices){
                list[i].isSelected = false
            }
            it.isSelected = true
        }
    }
}