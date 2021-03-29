package ru.hvost.news.presentation.fragments.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentFeedFilterBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.adapters.recycler.ArticlesFilterAdapter
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class FeedFilterFragment : Fragment() {

    private lateinit var binding: FragmentFeedFilterBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onInterestsLoadingEvent: DefaultNetworkEventObserver
    private var byUpdate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onInterestsLoadingSuccess() {
        val adapter = ArticlesFilterAdapter(mainVM)
        binding.list.adapter = adapter
        val interests = mainVM.interests.value?.toMutableList()
        val userInterests = mainVM.userData.value?.interests
        val actual = checkInterestsStates(interests, userInterests).toMutableList()
        actual.run {
            adapter.setFullList(this)
            adapter.submitList(this.filterIsInstance<InterestsCategory>())
        }
    }

    private fun checkInterestsStates(
        interests: List<CategoryItem>?,
        userInterests: List<String>?
    ): List<CategoryItem> {
        if (interests == null) return listOf()
        for (item in interests) {
            if (userInterests?.contains(item.id.toString()) == true) {
                when (item) {
                    is InterestsCategory -> {
                        item.state = CheckboxStates.SELECTED
                        item.sendParent = true
                    }
                    is Interests -> {
                        item.state = CheckboxStates.SELECTED
                    }
                }
            }
        }
        return interests
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.interestsLoadingEvent.value?.peekContent() == State.SUCCESS) onInterestsLoadingSuccess()
        initializeObservers()
        setObservers()
        setListeners()
        @Suppress("DEPRECATION")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!byUpdate) mainVM.dismissArticlesFilterCustomDialog.value = OneTimeEvent()
    }

    private fun setListeners() {
        binding.button.setOnClickListener { updateArticles() }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun initializeObservers() {
        onInterestsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onInterestsLoadingSuccess() }
        )
    }

    private fun setObservers() {
        mainVM.interestsLoadingEvent.observe(viewLifecycleOwner, onInterestsLoadingEvent)
        mainVM.filterRvChangedEvent.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { checkButtonEnable() })
    }

    private fun checkButtonEnable() {
        binding.button.isEnabled =
            !(binding.list.adapter as ArticlesFilterAdapter).getFullList()?.filter {
                when (it) {
                    is InterestsCategory -> it.state == CheckboxStates.SELECTED
                    is Interests -> it.state == CheckboxStates.SELECTED
                }
            }.isNullOrEmpty()
    }

    private fun updateArticles() {
        byUpdate = true
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
            }
        }
        mainVM.changeUserData(interests = sendList.joinToString())
        findNavController().popBackStack()
    }

}