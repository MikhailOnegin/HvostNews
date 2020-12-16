package ru.hvost.news.presentation.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
                setFeedTabSelected()
            }
            MainViewModel.Companion.ButtonSelected.DOMAINS -> {
                setDomainsTabSelected()
            }
            MainViewModel.Companion.ButtonSelected.NEWS -> {
                setNewsTabSelected()
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
                setFeedTabSelected()
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.feedListFragment, null, navOptions.build())
            }
            R.id.domains -> {
                mainVM.feedTabState = MainViewModel.Companion.ButtonSelected.DOMAINS
                setDomainsTabSelected()
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.domainsGridFragment, null, navOptions.build())
            }
            R.id.news -> {
                mainVM.feedTabState = MainViewModel.Companion.ButtonSelected.NEWS
                setNewsTabSelected()
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.newsListFragment, null, navOptions.build())
            }
        }
    }

    private fun setNewsTabSelected() {
        binding.articles.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.domains.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.news.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray1
            )
        )
        binding.articlesFilter.visibility = View.GONE
    }

    private fun setDomainsTabSelected() {
        binding.articles.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.domains.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray1
            )
        )
        binding.news.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.articlesFilter.visibility = View.GONE
    }

    private fun setFeedTabSelected() {
        binding.articles.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray1
            )
        )
        binding.domains.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.news.setTextColor(
            ContextCompat.getColor(
                App.getInstance(),
                R.color.gray4
            )
        )
        binding.articlesFilter.visibility = View.VISIBLE
    }
}