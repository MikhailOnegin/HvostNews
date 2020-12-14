package ru.hvost.news.presentation.fragments.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentNewsListBinding
import ru.hvost.news.presentation.adapters.ArticleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.articles.ArticlesFragment
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class NewsListFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsListBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onAllArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.allArticlesLoadingEvent.value?.peekContent() == State.SUCCESS) setRecyclerView()
        initializeObservers()
        setDecoration()
        setObservers()
    }

    private fun setObservers() {
        mainVM.allArticlesLoadingEvent.observe(viewLifecycleOwner, onAllArticlesLoadingEvent)
    }

    private fun initializeObservers() {
        onAllArticlesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setRecyclerView() }
        )
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString(ArticlesFragment.ARTICLE_ID, id)
            bundle.putString("TYPE", "INDIVIDUAL")
//            findNavController().navigate(R.id.action_newsFragment_to_articleDetailFragment, bundle)
        }
        val adapter = ArticleAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.allArticles.value?.filter { it.categoryTitle == "Новости" })
    }

    private fun setDecoration() {
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.largeMargin)?.toInt() ?: 0
                parent.adapter.run {
                    outRect.bottom = elementMargin
                }
            }
        })
    }
}