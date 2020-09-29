package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.LayoutArticleItemBinding
import ru.hvost.news.models.Article

class ArticleAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(FaqDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.getArticleVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(
        private val binding: LayoutArticleItemBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(articleItem: Article) {
            Glide
                .with(binding.root)
                .load(articleItem.img)
                .fitCenter()
                .into(binding.img)
            binding.title.text = articleItem.title
            binding.description.text = articleItem.description
            binding.category.text = articleItem.category
            binding.views.text = articleItem.views
            binding.likes.text = articleItem.likes

            binding.root.setOnClickListener { onClick.invoke(articleItem.id) }
        }

        companion object {
            fun getArticleVH(parent: ViewGroup, onClick: (Long) -> Unit): ArticleViewHolder {
                val binding = LayoutArticleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleViewHolder(binding, onClick)
            }
        }

    }

    class FaqDiffUtilCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

}