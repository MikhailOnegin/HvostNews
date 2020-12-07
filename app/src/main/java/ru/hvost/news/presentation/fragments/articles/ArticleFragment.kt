package ru.hvost.news.presentation.fragments.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentArticleBinding
import ru.hvost.news.models.toArticleContent
import ru.hvost.news.presentation.adapters.recycler.ArticleContentAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.createSnackbar

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
        setRecyclerView()
        val article = mainVM.allArticles.value?.firstOrNull {
            it.articleId == arguments?.getString(ArticlesFragment.ITEM_ID)
        }
        if(article == null) {
            createSnackbar(
                anchorView = binding.root,
                text = getString(R.string.cantFindArticle),
                onButtonClicked = { findNavController().popBackStack() }
            ).show()
        } else {
            val content = article.toArticleContent()
            (binding.recyclerView.adapter as ArticleContentAdapter).submitList(
                content
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

    fun setRecyclerView() {
        binding.recyclerView.apply {
            adapter = ArticleContentAdapter()
            addItemDecoration(LinearRvItemDecorations(R.dimen.largeMargin))
        }

    }

}