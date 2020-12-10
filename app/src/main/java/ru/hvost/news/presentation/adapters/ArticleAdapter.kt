package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutArticleItemBinding
import ru.hvost.news.models.Article

class ArticleAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.getArticleVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(
        private val binding: LayoutArticleItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(articleItem: Article) {
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + articleItem.imageUrl)
                .fitCenter()
                .into(binding.img)
            binding.title.text = articleItem.title
            binding.description.text = articleItem.shortDescription.parseAsHtml()
            binding.domain.text = articleItem.categoryTitle
            binding.views.text = articleItem.viewsCount.toString()
            binding.likes.text = articleItem.likesCount.toString()

            binding.img.doOnLayout {
                val width = binding.img.width
                val height = width / 1.625F
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height.toInt()
                )
                binding.img.layoutParams = params
            }
            if (articleItem.isLiked) {
                binding.likes.setCompoundDrawables(
                    ContextCompat.getDrawable(binding.root.context, R.drawable.ic_like_checked),
                    null,
                    null,
                    null
                )
            }
            binding.root.setOnClickListener { onClick.invoke(articleItem.articleId) }
        }

        companion object {
            fun getArticleVH(parent: ViewGroup, onClick: (String) -> Unit): ArticleViewHolder {
                val binding = LayoutArticleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleViewHolder(binding, onClick)
            }
        }

    }

    class ArticleDiffUtilCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

}