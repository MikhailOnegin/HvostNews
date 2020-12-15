package ru.hvost.news.presentation.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
        initializeObservers()
        setObservers()
        setListeners()
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
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.feedListFragment)
            }
            R.id.domains -> {
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
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.domainsGridFragment)
            }
            R.id.news -> {
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
                requireActivity().findNavController(R.id.fragmentContainerView)
                    .navigate(R.id.newsListFragment)
            }
        }
    }
}