package ru.hvost.news.presentation.fragments.articles

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentArticlesBinding
import ru.hvost.news.databinding.FragmentSubdomainBinding
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.adapters.ArticleAdapter
import ru.hvost.news.presentation.fragments.domains.SubDomainFragment
import ru.hvost.news.utils.enums.State

class ArticlesFragment : Fragment() {

    private lateinit var binding: FragmentArticlesBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var navC: NavController

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        if (arguments?.getString("CATEGORY") == "NEWS") binding.tabLayout.getTabAt(2)?.select()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        navC = findNavController()
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.filter.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_domainFragment)
        }
        binding.tabLayout.addOnTabSelectedListener(
            OnTabSelected(
                navC,
                binding,
                mainVM
            )
        )
    }

    private class OnTabSelected(
        private val nav: NavController,
        private val binding: FragmentArticlesBinding,
        private val mainVM: MainViewModel
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            updateList(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {
            updateList(tab)
        }

        private fun updateList(tab: TabLayout.Tab?) {
            when (tab?.text) {
                "Лента" -> loadArticles()
                "Статьи" -> nav.navigate(R.id.action_newsFragment_to_domainFragment)
                "Новости" -> loadNews()
            }
        }

        private fun loadArticles() {
            val adapter = binding.list.adapter as ArticleAdapter
            adapter.submitList(mainVM.articles.value)
        }

        private fun loadNews() {
            val adapter = binding.list.adapter as ArticleAdapter
            val originList = mainVM.allArticles.value ?: listOf()
            adapter.submitList(
                originList.filter { it.categoryId == "24" }
            )
        }
    }

    private fun setObservers() {
        mainVM.articlesState.observe(viewLifecycleOwner, Observer { onArticleStateChanged(it) })
    }

    private fun onArticleStateChanged(state: State) {
        when (state) {
            State.SUCCESS -> {
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setSystemUiVisibility()
        (requireActivity() as MainActivity).showBnv()
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: Long ->
            val bundle = Bundle()
            bundle.putLong("ITEM_ID", id)
            bundle.putString("TYPE", "INDIVIDUAL")
            findNavController().navigate(R.id.action_newsFragment_to_articleDetailFragment, bundle)
        }
        val adapter = ArticleAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.articles.value)
        setDecoration()
    }

    private fun setDecoration() {
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    if (position == 0) {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin

                    } else {
                        outRect.top = 0
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin
                    }
                }
            }
        })
    }
}