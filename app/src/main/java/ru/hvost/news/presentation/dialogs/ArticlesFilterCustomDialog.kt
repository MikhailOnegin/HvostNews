package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutArticlesFilterBinding
import ru.hvost.news.models.CategoryItem
import ru.hvost.news.models.CheckboxStates
import ru.hvost.news.models.FilterFooter
import ru.hvost.news.models.InterestsCategory
import ru.hvost.news.presentation.adapters.ArticlesFilterAdapter
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class ArticlesFilterCustomDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutArticlesFilterBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onInterestsLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutArticlesFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onInterestsLoadingSuccess() { //yunusov: обработать пустые интересы пользователя
        val adapter = ArticlesFilterAdapter(mainVM)
        val interests = mainVM.interests.value
        val userInterests = mainVM.userData.value?.interests
        val actual = checkInterestsStates(interests, userInterests)?.toMutableList()
        actual?.add(FilterFooter)
        binding.list.adapter = adapter
        adapter.submitList(actual)
    }

    private fun checkInterestsStates(
        interests: List<CategoryItem>?,
        userInterests: List<String>?
    ): List<CategoryItem>? {
        interests?.map { category ->
            if (userInterests?.contains((category as InterestsCategory).categoryId) == true) {
                (category as InterestsCategory).state = CheckboxStates.SELECTED
                category.sendParent = true
                category.interests.map { interest ->
                    interest.state = CheckboxStates.SELECTED
                }
            } else {
                (category as InterestsCategory).interests.map { interest ->
                    if (userInterests?.contains(interest.interestId) == true) {
                        category.state = CheckboxStates.INDETERMINATE
                        interest.state = CheckboxStates.SELECTED
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
        super.onActivityCreated(savedInstanceState)
    }

    private fun initializeObservers() {
        onInterestsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onInterestsLoadingSuccess() }
        )
    }

    private fun setObservers() {
        mainVM.interestsLoadingEvent.observe(viewLifecycleOwner, onInterestsLoadingEvent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
}