package ru.hvost.news.presentation.fragments.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFeedListBinding
import ru.hvost.news.models.Article
import ru.hvost.news.presentation.adapters.ArticleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.articles.ArticlesFragment
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class FeedListFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedListBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.articlesLoadingEvent.value?.peekContent() == State.SUCCESS) setRecyclerView()
        initializeObservers()
        setDecoration()
        setObservers()
    }

    private fun setObservers() {
        mainVM.articlesLoadingEvent.observe(viewLifecycleOwner, onArticlesLoadingEvent)
//        mainVM.articles.observe(viewLifecycleOwner, { onArticlesChanged() })
    }

//    private fun onArticlesChanged() {
//        val current = (binding.list.adapter as ArticleAdapter).currentList
//        if (current.isNullOrEmpty()) return
//        (binding.list.adapter as ArticleAdapter).submitList(current.toMutableList())
//        //yunusov: доделть коррекцию лайков и просмотров
//    }

    private fun initializeObservers() {
        onArticlesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setRecyclerView() }
        )
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString(ArticlesFragment.ARTICLE_ID, id)
            bundle.putString("TYPE", "INDIVIDUAL")
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