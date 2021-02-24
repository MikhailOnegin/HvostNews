package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.textView_what_wait
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentSeminarInfoBinding
import ru.hvost.news.databinding.LayoutSeminarWhatWaitBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.presentation.adapters.recycler.PartnersAdapter
import ru.hvost.news.presentation.adapters.recycler.VideoPastSeminarsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class SeminarInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentSeminarInfoBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var navCMain: NavController
    private val adapterVideo = VideoPastSeminarsAdapter()
    private val adapterPartners = PartnersAdapter()
    private var seminar: OfflineSeminars.OfflineSeminar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeminarInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        binding.recyclerViewVideo.adapter = adapterVideo
        binding.recyclerViewSponsors.adapter = adapterPartners
        binding.recyclerViewSponsors.layoutManager = GridLayoutManager(requireContext(), 2)
        setObservers(this)
        navCMain.previousBackStackEntry?.savedStateHandle?.set("fromDestination", "seminar")
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
        schoolVM.offlineSeminars.observe(owner, {
            seminar = it.seminars[0]
            seminar?.let { seminar ->
                Glide.with(requireContext())
                    .load(APIService.baseUrl + seminar.imageUrl)
                    .placeholder(R.drawable.ic_loader_spinner)
                    .error(R.drawable.load_error_padding)
                    .centerCrop()
                    .into(binding.imageViewSeminar)
                binding.textViewDescription.text = seminar.description.parseAsHtml()
                if (seminar.wait.isNotEmpty()) {
                    binding.constraintWhatWait.visibility = View.VISIBLE
                    val linearWait = binding.linearLayoutWhatWait
                    linearWait.removeAllViews()
                    for (i in seminar.wait.indices) {
                        val wait = seminar.wait[i]
                        val viewWait = LayoutSeminarWhatWaitBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            linearWait,
                            false
                        ).root
                        val margin = resources.getDimension(R.dimen.normalMargin).toInt() / 2
                        viewWait.textView_what_wait.text = wait.head
                        (viewWait.layoutParams as LinearLayout.LayoutParams).setMargins(
                            0,
                            0,
                            0,
                            margin
                        )
                        linearWait.addView(viewWait)
                    }
                }
                if (seminar.videos.isNotEmpty()) {
                    binding.constraintPastVideos.visibility = View.VISIBLE
                    adapterVideo.setVideos(seminar.videos)
                }

                if (seminar.partners.isNotEmpty()) {
                    binding.constraintPartners.visibility = View.VISIBLE
                    adapterPartners.setPartners(seminar.partners)
                }
            }
        })
    }
}