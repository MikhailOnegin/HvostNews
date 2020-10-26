package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.LayoutPrizeItemBinding
import ru.hvost.news.models.Prize

class PrizeAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Prize, PrizeAdapter.PetViewHolder>(PrizeDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder.getPrizeVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(
        private val binding: LayoutPrizeItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prize: Prize) {
            binding.title.text = prize.category

            binding.root.setOnClickListener { onClick.invoke(prize.prizeId) }
        }

        companion object {
            fun getPrizeVH(parent: ViewGroup, onClick: (String) -> Unit): PetViewHolder {
                val binding = LayoutPrizeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PetViewHolder(binding, onClick)
            }
        }

    }

    class PrizeDiffUtilCallback : DiffUtil.ItemCallback<Prize>() {
        override fun areItemsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem.prizeId == newItem.prizeId
        }

        override fun areContentsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem == newItem
        }
    }

}