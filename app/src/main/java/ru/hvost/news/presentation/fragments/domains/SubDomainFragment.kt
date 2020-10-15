package ru.hvost.news.presentation.fragments.domains

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSubdomainBinding
import ru.hvost.news.presentation.adapters.ArticleAdapter
import ru.hvost.news.utils.enums.State

class SubDomainFragment : Fragment() {

    private lateinit var binding: FragmentSubdomainBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubdomainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        setRecyclerView()
    }

    private var selectedPosition: Int? = null

    override fun onStop() {
        super.onStop()
        selectedPosition = binding.categoryTabs.selectedTabPosition
    }

    private fun setTabs() {
        val categories =
            mainVM.categories?.filter { it.domain == arguments?.getLong("DOMAIN_ID") } ?: return
        for (category in categories) {
            val tab = binding.categoryTabs.newTab()
            tab.tag = category.id
            tab.text = category.title
            binding.categoryTabs.addTab(tab)
        }
        setTabsListener()
        selectedPosition?.run {
            binding.categoryTabs.getTabAt(this)?.select()
        } ?: binding.categoryTabs.getTabAt(0)?.select()
    }

    private fun setObservers() {
        mainVM.allArticlesState.observe(viewLifecycleOwner, Observer { onAllArticlesChange(it) })
    }

    private fun onAllArticlesChange(state: State?) {
        when (state) {
            State.SUCCESS -> {
                setTabs()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setTabsListener() {
        binding.categoryTabs.addOnTabSelectedListener(OnTabSelected(binding, mainVM))
    }

    private class OnTabSelected(
        private val binding: FragmentSubdomainBinding,
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
            val adapter = binding.list.adapter as ArticleAdapter
            val originList = mainVM.allArticles.value ?: listOf()
            adapter.submitList(
                originList.filter { it.categoryId == tab?.tag.toString() }
            )
        }

    }

    private fun setRecyclerView() {
        val onActionClicked = { id: Long ->
            val bundle = Bundle()
            bundle.putLong("ITEM_ID", id)
            bundle.putString("TYPE", "ALL")
            findNavController().navigate(
                R.id.action_subDomainFragment_to_articleDetailFragment,
                bundle
            )
        }
        val adapter = ArticleAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.allArticles.value?.filter {
            it.domainId == arguments?.getLong("DOMAIN_ID").toString()
        })
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
