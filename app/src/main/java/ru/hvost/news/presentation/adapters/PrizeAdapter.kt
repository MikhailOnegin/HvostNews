package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.LayoutPrizeItemBinding
import ru.hvost.news.models.Prize

class PrizeAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Prize, PrizeAdapter.PetViewHolder>(FaqDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder.getPrizeVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(
        private val binding: LayoutPrizeItemBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prize: Prize) {
            binding.title.text = prize.name

            binding.root.setOnClickListener { onClick.invoke(prize.id) }
        }

        companion object {
            fun getPrizeVH(parent: ViewGroup, onClick: (Long) -> Unit): PetViewHolder {
                val binding = LayoutPrizeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PetViewHolder(binding, onClick)
            }
        }

    }

    class FaqDiffUtilCallback : DiffUtil.ItemCallback<Prize>() {
        override fun areItemsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem == newItem
        }
    }

}