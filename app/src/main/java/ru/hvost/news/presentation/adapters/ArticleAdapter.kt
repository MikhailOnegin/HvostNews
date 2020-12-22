package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
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

class ArticleAdapter(
    private val onClick: (String) -> Unit,
    private val setLiked: (String, Boolean) -> Unit
) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.getArticleVH(parent, onClick, setLiked)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(
        private val binding: LayoutArticleItemBinding,
        private val onClick: (String) -> Unit,
        private val setLiked: (String, Boolean) -> Unit
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
                binding.likesIcon.setImageResource(R.drawable.ic_like_checked)
            } else {
                binding.likesIcon.setImageResource(R.drawable.ic_likes)
            }
            binding.apply {
                imgContainer.setOnClickListener { onClick.invoke(articleItem.articleId) }
                container.setOnClickListener { onClick.invoke(articleItem.articleId) }
                likesIcon.setOnClickListener {
                    setLiked.invoke(articleItem.articleId, !articleItem.isLiked)
                    changeLikeIcon(articleItem)
                }
            }
        }

        private fun changeLikeIcon(item: Article) {
            item.isLiked = !item.isLiked
            if (item.isLiked) {
                binding.likesIcon.setImageResource(R.drawable.ic_like_checked)
                binding.likes.text = binding.likes.text.toString().toInt().plus(1).toString()
                item.likesCount = item.likesCount.plus(1)
            } else {
                binding.likesIcon.setImageResource(R.drawable.ic_likes)
                binding.likes.text = binding.likes.text.toString().toInt().minus(1).toString()
                item.likesCount = item.likesCount.minus(1)
            }
        }

        companion object {
            fun getArticleVH(
                parent: ViewGroup,
                onClick: (String) -> Unit,
                setLiked: (String, Boolean) -> Unit
            ): ArticleViewHolder {
                val binding = LayoutArticleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleViewHolder(binding, onClick, setLiked)
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