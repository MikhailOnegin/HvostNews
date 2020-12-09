package ru.hvost.news.presentation.fragments.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentArticleBinding
import ru.hvost.news.models.Article
import ru.hvost.news.models.toArticleContent
import ru.hvost.news.presentation.adapters.recycler.ArticleContentAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.events.Event

class ArticleFragment : BaseFragment() {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val article = mainVM.allArticles.value?.firstOrNull {
            it.articleId == arguments?.getString(ArticlesFragment.ITEM_ID)
        }
        setRecyclerView(article)
        setGUI(article)
        setObservers()
    }

    private fun setGUI(article: Article?) {
        if(article == null) {
            createSnackbar(
                anchorView = binding.root,
                text = getString(R.string.cantFindArticle),
                onButtonClicked = { findNavController().popBackStack() }
            ).show()
        } else {
            (binding.recyclerView.adapter as ArticleContentAdapter).submitList(
                article.toArticleContent()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        mainVM.shareArticleEvent.observe(viewLifecycleOwner) { onShareArticleEvent(it) }
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

    fun setRecyclerView(article: Article?) {
        binding.recyclerView.apply {
            adapter = ArticleContentAdapter(mainVM, isLiked = article?.isLiked ?: false)
            addItemDecoration(LinearRvItemDecorations(R.dimen.largeMargin))
        }
    }

}