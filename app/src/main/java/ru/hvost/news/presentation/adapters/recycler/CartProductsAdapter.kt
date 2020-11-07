package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.RvCartFooterBinding
import ru.hvost.news.databinding.RvCartProductBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.CartProduct
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class CartProductsAdapter
    : ListAdapter<CartItem, RecyclerView.ViewHolder>(CartProductDiffUtilItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_PRODUCT -> CartProductVH.getViewHolder(parent)
            TYPE_FOOTER -> CartFooterVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong adapter view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            TYPE_PRODUCT -> {
                (holder as CartProductVH).bind(getItem(position) as CartProduct, position, itemCount)
            }
            TYPE_FOOTER -> {
                (holder as CartFooterVH).bind(getItem(position) as CartFooter)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is CartProduct -> TYPE_PRODUCT
            is CartFooter -> TYPE_FOOTER
        }
    }

    class CartProductVH(
        val binding: RvCartProductBinding
    ): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartProduct, position: Int, size: Int) {
            Glide.with(binding.root).load(item.imageUri).into(binding.image)
            binding.run {
                title.text = item.title
                count.text = item.count.toString()
                price.text = "${moneyFormat.format(item.price.toInt())} \u20bd"
                cost.text = "${moneyFormat.format(item.price.toInt() * item.count)} \u20bd"
                if(position == size - 2) divider.visibility = View.GONE
                else divider.visibility = View.VISIBLE
                minus.setOnClickListener {  }
                plus.setOnClickListener {  }
            }
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

    class CartFooterVH(
        val binding: RvCartFooterBinding
    ): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartFooter) {
            binding.run {
                total.text = "${moneyFormat.format(item.total.toInt())} \u20bd"
                oldPrice.text = "${moneyFormat.format(item.oldPrice.toInt())} \u20bd"
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): CartFooterVH {
                val binding = RvCartFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CartFooterVH(binding)
            }

        }

    }

    class CartProductDiffUtilItemCallback : DiffUtil.ItemCallback<CartItem>() {

        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return if(oldItem is CartProduct && newItem is CartProduct){
                oldItem.id == newItem.id
            } else false
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return if(oldItem is CartProduct && newItem is CartProduct){
                oldItem == newItem
            } else false
        }
    }

    companion object {

        const val TYPE_PRODUCT = 1
        const val TYPE_FOOTER = 2

    }

}