package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        schoolVM = ViewModelProvider(this)[SchoolViewModel::class.java]
        setListeners()
        seminarId = arguments?.getString("seminarId")
        setObservers(this)
        setSystemUiVisibility()
    }

    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, Observer {
            seminarId?.run {
                for (i in it.seminars.indices){
                    val seminar = it.seminars[i]

                }
            }
        })
    }

    private fun setListeners() {
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