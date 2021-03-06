package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.content.DialogInterface
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
import ru.hvost.news.models.*
import ru.hvost.news.presentation.adapters.recycler.ArticlesFilterAdapter
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

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

    private fun onInterestsLoadingSuccess() {
        val adapter = ArticlesFilterAdapter(mainVM)
        binding.list.adapter = adapter
        val interests = mainVM.interests.value?.toMutableList()
        val userInterests = mainVM.userData.value?.interests
        val actual = checkInterestsStates(interests, userInterests).toMutableList()
        actual.run {
            adapter.setFullList(this)
            adapter.submitList(this.filter { it is InterestsCategory})
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mainVM.dismissArticlesFilterCustomDialog.value = OneTimeEvent()
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