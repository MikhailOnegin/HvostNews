package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutArticleItemBinding
import ru.hvost.news.models.Article
import ru.hvost.news.utils.getDefaultShimmer
import ru.hvost.news.utils.moneyFormat


class ArticleAdapter(
    private val onClick: (String) -> Unit,
    private val setLiked: (String, Boolean) -> Unit
) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.getArticleVH(parent, onClick, setLiked)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ArticleViewHolder(
        private val binding: LayoutArticleItemBinding,
        private val onClick: (String) -> Unit,
        private val setLiked: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(articleItem: Article, position: Int) {
            binding.root.setBackgroundResource(R.drawable.background_article)
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + articleItem.imageUrl)
                .placeholder(getDefaultShimmer(itemView.context))
                .error(R.drawable.empty_image)
                //.centerCrop()
                .into(binding.img)
            binding.title.text = articleItem.title
            binding.description.text = articleItem.shortDescription.parseAsHtml()
            binding.domain.text = articleItem.categoryTitle
            binding.views.text = moneyFormat.format(articleItem.viewsCount)
            binding.likes.text = moneyFormat.format(articleItem.likesCount)

            if (articleItem.isLiked) {
                binding.likesIcon.setImageResource(R.drawable.ic_feed_like_list_checked)
            } else {
                binding.likesIcon.setImageResource(R.drawable.ic_likes_list)
            }
            binding.apply {
                root.setOnClickListener { onClick.invoke(articleItem.articleId) }
                likesIcon.setOnClickListener {
                    setLiked.invoke(articleItem.articleId, !articleItem.isLiked)
                    changeLikeIcon(articleItem)
                }
                articleType.apply {
                    when (articleItem.postTypeId) {
                        ARTICLE_ID -> setImageResource(R.drawable.ic_arcticle)
                        ASK_ID -> setImageResource(R.drawable.ic_ask)
                        VIDEO_ID -> setImageResource(R.drawable.ic_video)
                        NEWS_ID -> setImageResource(R.drawable.ic_news)
                    }
                }
            }
        }

        private fun changeLikeIcon(item: Article) {
            item.isLiked = !item.isLiked
            if (item.isLiked) {
                binding.likesIcon.setImageResource(R.drawable.ic_feed_likes_checked)
                binding.likes.text =
                    moneyFormat.format(binding.likes.text.toString().toInt().plus(1))
                item.likesCount = item.likesCount.plus(1)
            } else {
                binding.likesIcon.setImageResource(R.drawable.ic_likes)
                binding.likes.text =
                    moneyFormat.format(binding.likes.text.toString().toInt().minus(1))
                item.likesCount = item.likesCount.minus(1)
            }
        }

        companion object {

            private val margin =
                App.getInstance().resources.getDimension(R.dimen.largeMargin).toInt()

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
                val width = parent.width - (margin * 2)
                val height = width / 1.5F
                val params = ConstraintLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height.toInt()
                )
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                binding.img.layoutParams = params
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

    companion object {
        const val NEWS_ID = "91544"
        const val ARTICLE_ID = "91545"
        const val VIDEO_ID = "91546"
        const val ASK_ID = "91547"
    }

}