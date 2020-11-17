package ru.hvost.news.presentation.fragments.articles

import android.content.Intent
import android.net.Uri
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
import ru.hvost.news.utils.imageRegEx
import java.util.regex.Pattern

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
        if (list != null) {

//            val CODE_REGEX = "^<img.*.\">"
//            val pattern = imageRegEx
//            println(pattern)
//            println(list[0].description)
//            println(pattern.matcher(list[0].description).matches())

            binding.title.text = list[0].title
            Glide
                    .with(binding.root)
                    .load(APIService.baseUrl + list[0].imageUrl)
                    .fitCenter()
                    .into(binding.img)
            binding.description.text = list[0].description.parseAsHtml()
            binding.likes.text = list[0].likesCount.toString()
            binding.views.text = list[0].viewsCount
            setShareButton(APIService.baseUrl + list[0].shareLink)
        }
    }

    private fun setShareButton(link: String) {
        binding.share.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(intent, "Link to article: "))
        }
    }
}