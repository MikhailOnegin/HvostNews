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
import ru.hvost.news.models.Interests
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
    private var areFiltersExpanded = true
    private var isAnimationRunning = false


    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showBnv()
        when (mainVM.feedTabState) {
            MainViewModel.Companion.ButtonSelected.FEED -> {
                setTabSelected(0)
                binding.articlesFilter.visibility = View.VISIBLE
            }
            MainViewModel.Companion.ButtonSelected.DOMAINS -> {
                areFiltersExpanded = false
                setTabSelected(1)
                goToDestination(R.id.domainsGridFragment)
                binding.articlesFilter.visibility = View.INVISIBLE
                binding.articlesFilter.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0
                )
            }
            MainViewModel.Companion.ButtonSelected.NEWS -> {
                areFiltersExpanded = false
                setTabSelected(2)
                goToDestination(R.id.newsListFragment)
                binding.articlesFilter.visibility = View.INVISIBLE
                binding.articlesFilter.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initializeNavOptions()
        initializeObservers()
        setObservers()
        setListeners()
        binding.toolbar.background.level = 1
        return binding.root
    }

    private fun initializeNavOptions() {
        navOptions
            .setEnterAnim(R.anim.fragment_enter)
            .setExitAnim(R.anim.fragment_exit)
            .setPopEnterAnim(R.anim.fragment_enter)
            .setPopExitAnim(R.anim.fragment_exit)
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
        mainVM.dismissArticlesFilterCustomDialog.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { clearAllInterests() })
        mainVM.updateArticlesWithNewInterests.observe(viewLifecycleOwner,
            OneTimeEvent.Observer { updateArticles() })
    }

    private fun closeDialog() {
        clearAllInterests()
        filterDialog.dismiss()
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
                else -> {
                }
            }
        }
    }

    private fun updateArticles() {
        val sendList: MutableList<String> = mutableListOf()
        mainVM.interests.value?.map {
            when (it) {
                is InterestsCategory -> {
                    if (it.state == CheckboxStates.SELECTED && it.sendParent)
                        sendList.add(it.categoryId.toString())
                }
                is Interests -> {
                    val parentId = mainVM.interests.value?.first { category ->
                        category is InterestsCategory && category.categoryId == it.parentCategoryId
                    }
                    if (it.state == CheckboxStates.SELECTED && !(parentId as InterestsCategory).sendParent)
                        sendList.add(it.interestId.toString())
                }
                else -> {
                }
            }
        }
        mainVM.changeUserData(interests = sendList.joinToString())
        clearAllInterests()
        closeDialog()
    }

    private val onSelectTabButton = { view: View ->
        if (!isAnimationRunning) {
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
    }

    private fun animateFiltersAndGoToDestination(
        shouldExpandFilters: Boolean,
        destination: Int
    ) {
        if ((shouldExpandFilters && areFiltersExpanded)
            || (!shouldExpandFilters && !areFiltersExpanded)
        ) {
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

        override fun onAnimationStart(animation: Animator?) {
            isAnimationRunning = true
            if (!shouldExpandFilters) binding.articlesFilter.visibility = View.INVISIBLE
        }

        override fun onAnimationEnd(animation: Animator?) {
            isAnimationRunning = false
            if (shouldExpandFilters) binding.articlesFilter.visibility = View.VISIBLE
            areFiltersExpanded = shouldExpandFilters
            goToDestination(destination)
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationRepeat(animation: Animator?) {}

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