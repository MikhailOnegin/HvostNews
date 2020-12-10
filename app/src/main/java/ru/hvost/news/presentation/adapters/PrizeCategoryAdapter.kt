package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.LayoutPrizeItemBinding
import ru.hvost.news.models.PrizeCategory

class PrizeCategoryAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<PrizeCategory, PrizeCategoryAdapter.PrizeCategoryViewHolder>(PrizeDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeCategoryViewHolder {
        return PrizeCategoryViewHolder.getPrizeVH(parent, onClick)
    }

    override fun onBindViewHolder(holderCategory: PrizeCategoryViewHolder, position: Int) {
        holderCategory.bind(getItem(position))
    }

    class PrizeCategoryViewHolder(
        private val binding: LayoutPrizeItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: PrizeCategory) {
            binding.title.text = category.prizeCategoryName.replace("Приз для", "")
            binding.root.setOnClickListener { onClick.invoke(category.prizeCategoryId) }

            binding.container.doOnLayout {
                val width = it.width
                val height = width / 5.4F
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height.toInt()
                )
                binding.container.layoutParams = params
            }
        }

        companion object {
            fun getPrizeVH(parent: ViewGroup, onClick: (String) -> Unit): PrizeCategoryViewHolder {
                val binding = LayoutPrizeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PrizeCategoryViewHolder(binding, onClick)
            }
        }

    }

    class PrizeDiffUtilCallback : DiffUtil.ItemCallback<PrizeCategory>() {
        override fun areItemsTheSame(oldItem: PrizeCategory, newItem: PrizeCategory): Boolean {
            return oldItem.prizeCategoryId == newItem.prizeCategoryId
        }

        override fun areContentsTheSame(oldItem: PrizeCategory, newItem: PrizeCategory): Boolean {
            return oldItem == newItem
        }
    }

}