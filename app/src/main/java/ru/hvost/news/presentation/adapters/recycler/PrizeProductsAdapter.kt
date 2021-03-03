package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.RvPrizeProductItemBinding
import ru.hvost.news.models.Prize
import ru.hvost.news.utils.getDefaultShimmer

class PrizeProductsAdapter() :
    ListAdapter<Prize.Product, PrizeProductsAdapter.PrizeProductViewHolder>(DomainDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeProductViewHolder {
        return PrizeProductViewHolder.getPrizeProductVH(parent)
    }

    override fun onBindViewHolder(holder: PrizeProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PrizeProductViewHolder(
        private val binding: RvPrizeProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Prize.Product) {
            binding.count.text = product.count.toString() + " шт"
            binding.title.text = product.title
            Glide.with(binding.root)
                    .load(APIService.baseUrl + product.imageUrl)
                    .placeholder(getDefaultShimmer(itemView.context))
                    .error(R.drawable.ic_load_error)
                    .fitCenter()
                    .into(binding.image)
        }

        companion object {
            fun getPrizeProductVH(parent: ViewGroup): PrizeProductViewHolder {
                val binding = RvPrizeProductItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PrizeProductViewHolder(binding)
            }
        }

    }

    class DomainDiffUtilCallback : DiffUtil.ItemCallback<Prize.Product>() {
        override fun areItemsTheSame(oldItem: Prize.Product, newItem: Prize.Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prize.Product, newItem: Prize.Product): Boolean {
            return oldItem == newItem
        }
    }

}