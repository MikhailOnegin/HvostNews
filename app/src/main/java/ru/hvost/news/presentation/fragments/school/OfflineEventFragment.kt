package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.layout_tab_item_seminar.view.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolOfflineEventBinding
import ru.hvost.news.databinding.LayoutTabItemSeminarBinding
import ru.hvost.news.presentation.dialogs.OfflineRegistrationDialog
import ru.hvost.news.presentation.dialogs.SuccessRegistrationEventDialog
import ru.hvost.news.presentation.dialogs.SuccessRegistrationSchoolDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.dateFormat
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class OfflineEventFragment : BaseFragment() {

    private lateinit var binding: FragmentSchoolOfflineEventBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var setSubscribeEvent: DefaultNetworkEventObserver
    private var seminarId: String? = null
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
        arguments?.getString("seminarTitle")?.let { seminarTitle ->
            schoolVM.successRegistration.value?.let { successRegistration ->
                if (successRegistration) {
                    App.getInstance().userToken?.run {
                        schoolVM.getOfflineSeminars("all", this)
                    }
                    schoolVM.successRegistration.value = false
                    SuccessRegistrationEventDialog(seminarTitle).show(
                        childFragmentManager,
                        "success_registration_dialog"
                    )
                }
            }

        }
        setListeners()
        seminarId = arguments?.getString("seminarId")
        initializedEvents()
        setObservers(this)
        binding.horizontalScrollView.isSmoothScrollingEnabled = false
        binding.horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
    }

    private fun initializedEvents() {
        setSubscribeEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = {binding.buttonParticipate.isEnabled = false },
            doOnSuccess = {
                binding.buttonParticipate.text = getString(R.string.you_are_subscribe_for_event)
                binding.buttonParticipate.isEnabled = false
            },
            doOnError = {
                binding.buttonParticipate.isEnabled = true
            },
            doOnFailure = {
                binding.buttonParticipate.isEnabled = true
            }
        )
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.seminarId.observe(owner,{seminarId ->
            schoolVM.offlineSeminars.value?.let {
                for (i in it.seminars.indices) {
                    val seminar = it.seminars[i]
                    if (seminar.id == seminarId) {
                        if (seminar.isFinished) {
                            if(!seminar.subscriptionEvent){
                                binding.buttonParticipate.text = getString(R.string.subscribe)
                                binding.buttonParticipate.setOnClickListener {
                                    App.getInstance().userToken?.let {userToken ->
                                        schoolVM.setSubscribe(userToken, seminarId.toString())
                                    }
                                }
                            }
                            else {
                                binding.buttonParticipate.text = getString(R.string.you_are_subscribe_for_event)
                                binding.buttonParticipate.isEnabled = false
                            }
                            binding.textViewLessonStatus.text = getString(R.string.completed)
                            binding.textViewLessonStatus.setTextColor(
                                    ContextCompat.getColor(
                                            requireContext(),
                                            R.color.red
                                    )
                            )

                        }
                        else {
                            binding.textViewLessonStatus.text = getString(R.string.active)
                            binding.textViewLessonStatus.setTextColor(
                                    ContextCompat.getColor(
                                            requireContext(),
                                            R.color.colorPrimary
                                    )
                            )
                            if (seminar.participate) {
                                binding.buttonParticipate.text = getString(R.string.you_participate)
                                binding.buttonParticipate.isEnabled = false
                            }
                            else {
                                binding.buttonParticipate.text = getString(R.string.participate)
                                binding.buttonParticipate.isEnabled = true
                                binding.buttonParticipate.isEnabled = true
                                binding.buttonParticipate.setOnClickListener {
                                    seminarId.run {
                                        val bundle = Bundle()
                                        bundle.putString("seminarId", this.toString())
                                        findNavController().navigate(R.id.action_offlineEventFragment_to_registrationFragment, bundle)
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
                            setListenerTab(viewTab2, tabsView, petTypeName)
                            linearTabs.addView(viewTab2)
                        }
                        binding.textViewTitle.text = seminar.title.parseAsHtml()
                        binding.textViewCity.text = seminar.city
                        binding.textViewDate.text = dateFormat(seminar.date)
                        binding.textViewSponsor.text = seminar.sponsor
                    }
                }
            }

        })
        schoolVM.changeFragment.observe(owner,{
            binding.nestedScrollView.scrollTo(0, 0);
        })
        schoolVM.setSubscribeEvent.observe(owner, setSubscribeEvent)
        schoolVM.offlineSeminars.observe(owner, {
            for(i in it.seminars.indices){
                val seminar = it.seminars[i]
                seminarId?.let {seminarId ->
                    if(seminar.id.toString() == seminarId){
                        if (seminar.participate) {
                            binding.buttonParticipate.text = getString(R.string.you_participate)
                            binding.buttonParticipate.isEnabled = false
                        }
                        else {
                            binding.buttonParticipate.text = getString(R.string.participate)
                            binding.buttonParticipate.isEnabled = true
                            binding.buttonParticipate.isEnabled = true
                            binding.buttonParticipate.setOnClickListener {
                                seminarId.run {
                                    val bundle = Bundle()
                                    bundle.putString("seminarId", seminarId)
                                    findNavController().navigate(R.id.action_offlineEventFragment_to_registrationFragment, bundle)
                                }

                            }
                        }
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

    private fun setListenerTab(view: View, list: List<View>, petTypeName: String? = null) {
        view.setOnClickListener {
            if (!it.isSelected) {
                petTypeName?.let {
                    schoolVM.petTypeName.value = petTypeName
                }
                if (list.size > 1) {
                    val navC = requireActivity().findNavController(R.id.fragmentViewSeminar)
                    if (list[0].isSelected) {
                        if (view != list[0]) navC.navigate(R.id.action_eventInfoFragment_to_eventScheduleFragment)
                    } else {
                        if (view == list[0]) navC.navigate(R.id.action_eventScheduleFragment_to_eventInfoFragment)
                        else navC.navigate(R.id.action_eventScheduleFragment_to_eventScheduleFragment)
                    }
                    for (i in list.indices) list[i].isSelected = false
                    it.isSelected = true
                }
                schoolVM.changeFragment.value = true
            }
        }
    }
}