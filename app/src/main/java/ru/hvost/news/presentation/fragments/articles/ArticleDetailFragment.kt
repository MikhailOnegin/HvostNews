package ru.hvost.news.presentation.fragments.articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentArticleDetailBinding
import ru.hvost.news.models.Article

class ArticleDetailFragment : Fragment() {

    private lateinit var binding: FragmentArticleDetailBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        findToBind()
    }

    private fun findToBind() {
        val type = arguments?.getString("TYPE")
        val id = arguments?.getLong("ITEM_ID")
        if (type == "ALL") {
            bind(mainVM.allArticles.value?.filter { it.id == id })
        } else if (type == "INDIVIDUAL") {
            bind(mainVM.articles.value?.filter { it.id == id })
        }

    }

    private fun bind(list: List<Article>?) {
        if(list != null){
            binding.title.text = list[0].title
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + list[0].imageUrl)
                .fitCenter()
                .into(binding.img)
            binding.description.text = list[0].shortDescription.parseAsHtml()
            binding.likes.text = list[0].likesCount.toString()
            binding.views.text = list[0].viewsCount
        }
    }
}