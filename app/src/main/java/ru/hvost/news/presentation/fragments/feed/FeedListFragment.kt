package ru.hvost.news.presentation.fragments.feed

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFeedListBinding
import ru.hvost.news.models.Article
import ru.hvost.news.presentation.adapters.recycler.ArticleAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.events.OneTimeEvent

class FeedListFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedListBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        binding.list.addItemDecoration(
                LinearRvItemDecorations(
                        sideMarginsDimension = R.dimen.normalMargin,
                        marginBetweenElementsDimension = R.dimen.extraLargeMargin,
                        drawTopMarginForFirstElement = true
                )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION")
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
    }

    private fun setObservers() {
        mainVM.articles.observe(viewLifecycleOwner, { setRecyclerView(it) })
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

    private fun setRecyclerView(list: List<Article>) {
        if (list.isNullOrEmpty()) {
            ObjectAnimator.ofFloat(
                    binding.list,
                    "alpha",
                    1f, 0f
            ).apply {
                duration = 300L
            }.start()
            binding.list.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(
                    binding.empty,
                    "alpha",
                    0f, 1f
            ).apply {
                duration = 300L
            }.start()
        } else {
            binding.list.visibility = View.VISIBLE
            binding.list.alpha = 1f
            binding.empty.visibility = View.GONE
        }
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString(FeedRedesignFragment.ARTICLE_ID, id)
            requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_feedFragment_to_articleDetailFragment, bundle)
        }
        val onActionLiked = { id: String, isLiked: Boolean ->
            mainVM.setArticleLiked(id, isLiked)
        }
        val adapter = ArticleAdapter(onActionClicked, onActionLiked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.articles.value)
        binding.list.scheduleLayoutAnimation()
    }

}