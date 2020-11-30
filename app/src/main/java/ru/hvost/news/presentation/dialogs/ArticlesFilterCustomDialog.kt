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
import ru.hvost.news.presentation.adapters.ArticlesFilterAdapter

class ArticlesFilterCustomDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutArticlesFilterBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutArticlesFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val onInterestsLoadingSuccess = {
        val adapter = ArticlesFilterAdapter()
        binding.list.adapter = adapter
        adapter.submitList(mainVM.interests.value)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainVM.interests.observe(viewLifecycleOwner, { onInterestsLoadingSuccess.invoke() })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
}