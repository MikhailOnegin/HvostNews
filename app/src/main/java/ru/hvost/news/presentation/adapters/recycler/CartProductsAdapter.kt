package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.RvCartProductBinding
import ru.hvost.news.models.CartProduct

class CartProductsAdapter : ListAdapter<CartProduct, CartProductsAdapter.CartProductVH>(CartProductDiffUtilItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductVH {
        return CartProductVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CartProductVH, position: Int) {
        holder.bind(getItem(position), position, itemCount)
    }

    class CartProductVH(
        val binding: RvCartProductBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartProduct, position: Int, size: Int) {
            Glide.with(binding.root).load(item.imageUri).into(binding.image)
            binding.title.text = item.title
            binding.minus.setOnClickListener {  }
            binding.plus.setOnClickListener {  }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): CartProductVH {
                val binding = RvCartProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CartProductVH(binding)
            }

        }

    }

    class CartProductDiffUtilItemCallback : DiffUtil.ItemCallback<CartProduct>() {

        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

}