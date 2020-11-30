package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.RvFilterFooterBinding
import ru.hvost.news.databinding.RvFilterItemBinding
import ru.hvost.news.databinding.RvFilterItemInterestBinding
import ru.hvost.news.models.*
import java.lang.IllegalArgumentException

class ArticlesFilterAdapter() :
    ListAdapter<CategoryItem, RecyclerView.ViewHolder>(
        ArticleFilterDiffUtilCallback()
    ) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InterestsCategory -> TYPE_CATEGORY
            is FilterFooter -> TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY -> CategoryItemViewHolder.getCategoryVH(parent)
            TYPE_FOOTER -> FooterViewHolder.getFooterVH(parent)
            else -> throw IllegalArgumentException("Wrong voucher view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is InterestsCategory -> (holder as CategoryItemViewHolder).bind(getItem(position) as InterestsCategory)
            is FilterFooter -> (holder as FooterViewHolder).bind()
        }
    }

    class CategoryItemViewHolder(
        private val binding: RvFilterItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InterestsCategory) {
            binding.expand.setOnClickListener { switchVisibility() }
            binding.interests.removeAllViews()
            binding.mainCheckbox.text = item.categoryName
            for (interest in item.interests) {
                val view = RvFilterItemInterestBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    null,
                    false
                )
                view.checkbox.text = interest.interestName
                binding.interests.addView(view.root)
            }
        }

        private fun switchVisibility() {
            when (binding.interests.visibility) {
                View.GONE -> binding.interests.visibility = View.VISIBLE
                View.VISIBLE -> binding.interests.visibility = View.GONE
            }
        }

        companion object {
            fun getCategoryVH(
                parent: ViewGroup
            ): CategoryItemViewHolder {
                val binding = RvFilterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryItemViewHolder(binding)
            }
        }
    }

    class FooterViewHolder(
        private val binding: RvFilterFooterBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.cancel.setOnClickListener { createToast }
            binding.show.setOnClickListener { createToast }
        }

        private val createToast = {
            Toast.makeText(binding.root.context, "Clicked!", Toast.LENGTH_SHORT).show()
        }

        companion object {
            fun getFooterVH(
                parent: ViewGroup,
            ): FooterViewHolder {
                val binding = RvFilterFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FooterViewHolder(binding)
            }
        }
    }

    class ArticleFilterDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return if (oldItem is InterestsCategory && newItem is InterestsCategory) {
                oldItem.categoryId == newItem.categoryId
            } else oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return false
        }
    }

    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_FOOTER = 1
    }

}