package ru.hvost.news.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutArticlesFilterBinding
import ru.hvost.news.models.CategoryItem
import ru.hvost.news.models.CheckboxStates
import ru.hvost.news.models.FilterFooter
import ru.hvost.news.models.InterestsCategory
import ru.hvost.news.presentation.adapters.ArticlesFilterAdapter

class ArticlesFilterCustomDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutArticlesFilterBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutArticlesFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val onInterestsLoadingSuccess = {
        val adapter = ArticlesFilterAdapter(mainVM)
        val interests = mainVM.interests.value
        val userInterests = mainVM.userDataResponse.value?.interests
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
                (category as InterestsCategory).sendParent = true
            } else {
                (category as InterestsCategory).interests.map { interest ->
                    if (userInterests?.contains(interest.interestId) == true) {
                        interest.state = CheckboxStates.SELECTED
                    }
                }
            }
        }
        return interests
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setObservers() {
        mainVM.interests.observe(viewLifecycleOwner, { onInterestsLoadingSuccess.invoke() })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
}