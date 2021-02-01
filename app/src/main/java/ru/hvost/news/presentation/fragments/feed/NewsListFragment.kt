package ru.hvost.news.presentation.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentNewsListBinding
import ru.hvost.news.models.Article
import ru.hvost.news.presentation.adapters.recycler.ArticleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class NewsListFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsListBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onAllArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        binding.root.addItemDecoration(
            LinearRvItemDecorations(
                sideMarginsDimension = R.dimen.largeMargin,
                marginBetweenElementsDimension = R.dimen.extraLargeMargin,
                drawTopMarginForFirstElement = false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.allArticlesLoadingEvent.value?.peekContent() == State.SUCCESS) setRecyclerView()
        initializeObservers()
        setObservers()
    }

    private fun setObservers() {
        mainVM.allArticlesLoadingEvent.observe(viewLifecycleOwner, onAllArticlesLoadingEvent)
        mainVM.likedAllArticles.observe(viewLifecycleOwner, { onAllArticlesChanged(it) })
        mainVM.updateArticlesViewsCount.observe(viewLifecycleOwner,
            OneTimeEvent.Observer { updateAllArticles() })
    }

    private fun updateAllArticles() {
        mainVM.allArticles.value?.let { onAllArticlesChanged(it) }
    }

    private fun onAllArticlesChanged(list: List<Article>?) {
        val adapter = (binding.list.adapter as ArticleAdapter)
        adapter.submitList(list?.filter { it.categoryTitle == "Новости" })
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
            bundle.putString(FeedFragment.ARTICLE_ID, id)
            requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_feedFragment_to_articleDetailFragment, bundle)
        }
        val onActionLiked = { id: String, isLiked: Boolean ->
            mainVM.setArticleLiked(id, isLiked)
        }
        val adapter = ArticleAdapter(onActionClicked, onActionLiked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.allArticles.value?.filter { it.categoryTitle == "Новости" })
    }

}