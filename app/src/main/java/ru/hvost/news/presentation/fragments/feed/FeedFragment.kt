package ru.hvost.news.presentation.fragments.feed

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFeedBinding
import ru.hvost.news.models.CheckboxStates
import ru.hvost.news.models.InterestsCategory
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.dialogs.ArticlesFilterCustomDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class FeedFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onChangeUserDataLoadingEvent: DefaultNetworkEventObserver
    private val filterDialog = ArticlesFilterCustomDialog()
    private val navOptions = NavOptions.Builder()


    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showBnv()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initializeNavOptions()
        checkTabsState()
        initializeObservers()
        setObservers()
        setListeners()
    }

    private fun initializeNavOptions() {
        navOptions
            .setEnterAnim(R.anim.fragment_enter)
            .setExitAnim(R.anim.fragment_exit)
            .setPopEnterAnim(R.anim.fragment_enter)
            .setPopExitAnim(R.anim.fragment_exit)
    }

    private fun checkTabsState() {
        when (mainVM.feedTabState) {
            MainViewModel.Companion.ButtonSelected.FEED -> {
                setTabSelected(0)
            }
            MainViewModel.Companion.ButtonSelected.DOMAINS -> {
                setTabSelected(1)
            }
            MainViewModel.Companion.ButtonSelected.NEWS -> {
                setTabSelected(2)
            }
        }
    }

    private fun initializeObservers() {
        onChangeUserDataLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                mainVM.loadArticles()
                mainVM.loadUserData()
            }
        )
    }

    private fun setListeners() {
        binding.articles.setOnClickListener(onSelectTabButton)
        binding.domains.setOnClickListener(onSelectTabButton)
        binding.news.setOnClickListener(onSelectTabButton)
        binding.filter.setOnClickListener { filterDialog.show(childFragmentManager, "info_dialog") }
    }

    private fun setObservers() {
        mainVM.changeUserDataLoadingEvent.observe(viewLifecycleOwner, onChangeUserDataLoadingEvent)
        mainVM.closeArticlesFilterCustomDialog.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { closeDialog() })
        mainVM.updateArticlesWithNewInterests.observe(viewLifecycleOwner,
            OneTimeEvent.Observer { updateArticles() })
    }

    private fun closeDialog() {
        mainVM.interests.value?.map { category ->
            (category as InterestsCategory).sendParent = false
            category.state = CheckboxStates.UNSELECTED
            category.interests.map { interest ->
                interest.state = CheckboxStates.UNSELECTED
            }
        }
        filterDialog.dismiss()
    }

    private fun updateArticles() {
        val interests = mainVM.interests.value ?: listOf()
        val sendList: MutableList<String> = mutableListOf()
        interests.map { category ->
            if ((category as InterestsCategory).sendParent) {
                sendList.add(category.categoryId)
            } else {
                category.interests.map { interest ->
                    if (interest.state == CheckboxStates.SELECTED) {
                        sendList.add(interest.interestId)
                    }
                }
            }
        }
        mainVM.changeUserData(interests = sendList.joinToString())
        closeDialog()
    }

    private val onSelectTabButton = { view: View ->
        when (view.id) {
            R.id.articles -> {
                mainVM.feedTabState = MainViewModel.Companion.ButtonSelected.FEED
                setTabSelected(0)
                animateFiltersAndGoToDestination(true, R.id.feedListFragment)
            }
            R.id.domains -> {
                mainVM.feedTabState = MainViewModel.Companion.ButtonSelected.DOMAINS
                setTabSelected(1)
                animateFiltersAndGoToDestination(false, R.id.domainsGridFragment)
            }
            R.id.news -> {
                mainVM.feedTabState = MainViewModel.Companion.ButtonSelected.NEWS
                setTabSelected(2)
                animateFiltersAndGoToDestination(false, R.id.newsListFragment)
            }
        }
    }

    private var areFiltersExpanded = true

    private fun animateFiltersAndGoToDestination(
        shouldExpandFilters: Boolean,
        destination: Int
    ) {
        if ((shouldExpandFilters && areFiltersExpanded)
            || (!shouldExpandFilters && !areFiltersExpanded)) {
            goToDestination(destination)
            return
        }
        binding.articlesFilter.measure(
            MeasureSpec.makeMeasureSpec(binding.filtersContainer.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(binding.filtersContainer.height, MeasureSpec.AT_MOST)
        )
        val height = binding.articlesFilter.measuredHeight
        val startValue = if (shouldExpandFilters) 0 else binding.articlesFilter.height
        val endValue = if (shouldExpandFilters) height else 0
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = resources.getInteger(R.integer.filtersContainerAnimationTime).toLong()
        animator.addUpdateListener {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                it.animatedValue as Int
            )
            binding.articlesFilter.layoutParams = params
        }
        animator.addListener(FiltersAnimationListener(shouldExpandFilters, destination))
        animator.start()
    }

    inner class FiltersAnimationListener(
        private val shouldExpandFilters: Boolean,
        private val destination: Int
    ) : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator?) { }

        override fun onAnimationEnd(animation: Animator?) {
            areFiltersExpanded = shouldExpandFilters
            goToDestination(destination)
        }

        override fun onAnimationCancel(animation: Animator?) { }

        override fun onAnimationRepeat(animation: Animator?) { }

    }

    private fun goToDestination(destination: Int) {
        requireActivity().findNavController(R.id.fragmentContainerView)
            .navigate(destination, null, navOptions.build())
    }

    private fun setTabSelected(position: Int) {
        binding.articles.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                if (position == 0) R.color.gray1 else R.color.gray4
            )
        )
        binding.domains.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                if (position == 1) R.color.gray1 else R.color.gray4
            )
        )
        binding.news.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                if (position == 2) R.color.gray1 else R.color.gray4
            )
        )
    }

}