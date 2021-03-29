package ru.hvost.news.presentation.fragments.feed

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFeedRedesignBinding
import ru.hvost.news.models.CheckboxStates
import ru.hvost.news.models.Interests
import ru.hvost.news.models.InterestsCategory
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class FeedRedesignFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedRedesignBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var mainVM: MainViewModel
    private lateinit var onChangeUserDataLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showBnv()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedRedesignBinding.inflate(inflater, container, false)
        setViewPager()
        initializeObservers()
        setObservers()
        setListeners()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private fun setListeners() {
        binding.filter.setOnClickListener { findNavController().navigate(R.id.action_feedRedesignFragment_to_feedFilterFragment) }
    }

    private fun setViewPager() {
        viewPager = binding.viewPager
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.bnvFeed)
                1 -> tab.text = getString(R.string.articlesTitle)
                2 -> tab.text = getString(R.string.news)
            }
        }.attach()
    }

    private fun initializeObservers() {
        onChangeUserDataLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = {
                binding.viewPager.alpha = 0f
                binding.progress.visibility = View.VISIBLE
            },
            doOnSuccess = {
                mainVM.loadArticles()
                mainVM.loadUserData()
            }
        )
        onArticlesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { animateContentAppear() },
            doOnFailure = { animateContentAppear() },
            doOnError = { animateContentAppear() }
        )
    }

    private fun animateContentAppear() {
        binding.progress.visibility = View.GONE
        ObjectAnimator.ofFloat(
            binding.viewPager,
            "alpha",
            0f, 1f
        ).apply {
            duration = 300L
        }.start()
    }

    private fun setObservers() {
        mainVM.changeUserDataLoadingEvent.observe(viewLifecycleOwner, onChangeUserDataLoadingEvent)
        mainVM.articlesLoadingEvent.observe(viewLifecycleOwner, onArticlesLoadingEvent)
        mainVM.dismissArticlesFilterCustomDialog.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { clearAllInterests() })
    }

    private fun clearAllInterests() {
        mainVM.interests.value?.map {
            when (it) {
                is InterestsCategory -> {
                    it.state = CheckboxStates.UNSELECTED
                    it.sendParent = false
                    it.isExpanded = false
                }
                is Interests -> it.state = CheckboxStates.UNSELECTED
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FeedListFragment()
                1 -> DomainsGridFragment()
                2 -> NewsListFragment()
                else -> FeedListFragment()
            }
        }
    }

    companion object {
        const val ARTICLE_ID = "article_id"
    }

}