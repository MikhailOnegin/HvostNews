package ru.hvost.news.presentation.fragments.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFeedListBinding
import ru.hvost.news.models.Article
import ru.hvost.news.presentation.adapters.recycler.ArticleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class FeedListFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedListBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        binding.root.addItemDecoration(
            LinearRvItemDecorationCustom(
                sideMarginsDimension = R.dimen.largeMargin,
                marginBetweenElementsDimension = R.dimen.extraLargeMargin
            )
        )
        return binding.root
    }

    class LinearRvItemDecorationCustom(
        sideMarginsDimension: Int? = null,
        marginBetweenElementsDimension: Int? = null
    ) : LinearRvItemDecorations() {
        private val res = App.getInstance().resources
        private val sideMargins =
            if (sideMarginsDimension != null)
                res.getDimension(sideMarginsDimension).toInt()
            else 0
        private val verticalMargin =
            if (marginBetweenElementsDimension != null)
                res.getDimension(marginBetweenElementsDimension).toInt()
            else 0

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(
                sideMargins,
                0,
                sideMargins,
                verticalMargin
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.articlesLoadingEvent.value?.peekContent() == State.SUCCESS) setRecyclerView()
        initializeObservers()
        setObservers()
    }

    private fun setObservers() {
        mainVM.articlesLoadingEvent.observe(viewLifecycleOwner, onArticlesLoadingEvent)
        mainVM.likedArticleList.observe(viewLifecycleOwner, { onArticlesChanged(it) })
        mainVM.updateArticlesViewsCount.observe(viewLifecycleOwner,
            OneTimeEvent.Observer { updateArticles() })
    }

    private fun updateArticles() {
        mainVM.articles.value?.let { onArticlesChanged(it) }
    }

    private fun onArticlesChanged(list: List<Article>?) {
        val adapter = (binding.list.adapter as ArticleAdapter)
        adapter.submitList(list)
    }

    private fun initializeObservers() {
        onArticlesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setRecyclerView() }
        )
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString(FeedFragment.ARTICLE_ID, id)
            requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_feedFragment_to_articleDetailFragment, bundle)
        }
        val onActionLiked = { id: String, isLiked: Boolean ->
            mainVM.setArticleLiked(id, isLiked)
        }
        val adapter = ArticleAdapter(onActionClicked, onActionLiked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.articles.value)
    }

}