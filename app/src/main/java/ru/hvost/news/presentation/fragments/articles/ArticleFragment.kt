package ru.hvost.news.presentation.fragments.articles

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentArticleBinding
import ru.hvost.news.models.Article
import ru.hvost.news.models.toArticleContent
import ru.hvost.news.presentation.adapters.recycler.ArticleContentAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.OneTimeEvent

class ArticleFragment : BaseFragment() {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var articleVM: ArticleViewModel
    private lateinit var loadingArticleEventObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        setViews()
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        articleVM = ViewModelProvider(this)[ArticleViewModel::class.java]
        articleVM.loadArticle(arguments?.getString(ArticlesFragment.ARTICLE_ID))
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        articleVM.apply {
            shareArticleEvent.observe(viewLifecycleOwner) { onShareArticleEvent(it) }
            article.observe(viewLifecycleOwner) { onArticleChanged(it) }
            loadArticleEvent.observe(viewLifecycleOwner, loadingArticleEventObserver)
            recyclerViewReadyEvent.observe(viewLifecycleOwner) { onRecyclerViewReadyEvent(it) }
        }
    }

    private fun onRecyclerViewReadyEvent(event: OneTimeEvent?) {
        event?.getEventIfNotHandled()?.run {
            binding.progress.visibility = View.GONE
            ObjectAnimator.ofFloat(
                binding.recyclerView,
                "alpha",
                0f, 1f
            ).apply {
                duration = 300L
            }.start()
        }
    }

    private fun onArticleChanged(article: Article?) {
        if(article == null) {
            createSnackbar(
                anchorView = binding.root,
                text = getString(R.string.cantFindArticle),
                onButtonClicked = { findNavController().popBackStack() }
            ).show()
        } else {
            binding.recyclerView.apply {
                val contentAdapter = ArticleContentAdapter(
                    articleVM = articleVM,
                    isLiked = article.isLiked
                )
                adapter = contentAdapter
                contentAdapter.submitList(article.toArticleContent())
            }
        }
    }

    private fun onShareArticleEvent(event: Event<String>?) {
        event?.getContentIfNotHandled()?.also {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
            val chooserIntent = Intent.createChooser(intent, getString(R.string.shareArticleTitle))
            requireActivity().startActivity(chooserIntent)
        }
    }

    private fun setViews() {
        binding.apply {
            recyclerView.addItemDecoration(LinearRvItemDecorations(R.dimen.largeMargin))
            recyclerView.alpha = 0f
            progress.visibility = View.VISIBLE
        }
    }

    private fun initializeObservers() {
        loadingArticleEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnError = { findNavController().popBackStack() },
            doOnFailure = { findNavController().popBackStack() }
        )
    }

}