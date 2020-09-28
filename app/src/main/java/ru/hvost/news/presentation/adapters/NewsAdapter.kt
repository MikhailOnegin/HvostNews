package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.LayoutNewsItemBinding
import ru.hvost.news.models.Article

class NewsAdapter(private val onClick: (String) -> Unit) : ListAdapter<Article, NewsAdapter.NewsViewHolder>(FaqDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.getNewsVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.setData(getItem(position))
    }

    class NewsViewHolder(
        private val binding: LayoutNewsItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(newItem: Article){
            Glide
                .with(binding.root)
                .load(newItem.img)
                .fitCenter()
                .into(binding.img)
            binding.title.text = newItem.title
            binding.description.text = newItem.description
            binding.category.text = newItem.category
            binding.views.text = newItem.views
            binding.likes.text = newItem.likes

//            binding.root.setOnClickListener { onClick.invoke(newItem.question ?: "") }
        }

        companion object {
            fun getNewsVH(parent: ViewGroup, onClick: (String) -> Unit): NewsViewHolder {
                val binding = LayoutNewsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return NewsViewHolder(binding, onClick)
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