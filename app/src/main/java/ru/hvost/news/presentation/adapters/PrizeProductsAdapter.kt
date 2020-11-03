package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutPrizeProductItemBinding
import ru.hvost.news.models.Prize

class PrizeProductsAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Prize.Product, PrizeProductsAdapter.PrizeProductViewHolder>(PrizeProductDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeProductViewHolder {
        return PrizeProductViewHolder.getPrizeVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PrizeProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PrizeProductViewHolder(
        private val binding: LayoutPrizeProductItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Prize.Product) {
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + product.imageUrl)
                .centerCrop()
                .into(binding.img)
            binding.title.text = product.title
            binding.count.text = product.count.toString()

//            binding.root.setOnClickListener { onClick.invoke(prize.prizeId) }
        }

        companion object {
            fun getPrizeVH(parent: ViewGroup, onClick: (String) -> Unit): PrizeProductViewHolder {
                val binding = LayoutPrizeProductItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PrizeProductViewHolder(binding, onClick)
            }
        }

    }

    class PrizeProductDiffUtilCallback : DiffUtil.ItemCallback<Prize.Product>() {
        override fun areItemsTheSame(oldItem: Prize.Product, newItem: Prize.Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prize.Product, newItem: Prize.Product): Boolean {
            return oldItem == newItem
        }
    }

}