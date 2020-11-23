package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.LayoutPrizePriceItemBinding
import ru.hvost.news.models.Prize

class PrizeAdapter(private val onClick: (Prize) -> Unit) :
    ListAdapter<Prize, PrizeAdapter.PrizeProductViewHolder>(PrizeProductDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeProductViewHolder {
        return PrizeProductViewHolder.getPrizeVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PrizeProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PrizeProductViewHolder(
        private val binding: LayoutPrizePriceItemBinding,
        private val onClick: (Prize) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prize: Prize) {
            binding.cost.text = prize.prizeCost
            binding.root.setOnClickListener { onClick.invoke(prize) }
        }

        companion object {
            fun getPrizeVH(parent: ViewGroup, onClick: (Prize) -> Unit): PrizeProductViewHolder {
                val binding = LayoutPrizePriceItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PrizeProductViewHolder(binding, onClick)
            }
        }

    }

    class PrizeProductDiffUtilCallback : DiffUtil.ItemCallback<Prize>() {
        override fun areItemsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem.prizeId == newItem.prizeId
        }

        override fun areContentsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem == newItem
        }
    }

}