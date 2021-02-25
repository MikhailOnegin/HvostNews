package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.RvOrderFooterBinding
import ru.hvost.news.databinding.RvOrderProductBinding
import ru.hvost.news.models.OrderFooter
import ru.hvost.news.models.OrderItem
import ru.hvost.news.models.Product
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class OrderProductsAdapter(
    private val deleteOrder: (Long) -> Unit
) : ListAdapter<OrderItem, RecyclerView.ViewHolder>(OrderItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_PRODUCT -> OrderProductVH.getViewHolder(parent)
            TYPE_FOOTER -> OrderFooterVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong OrderProductsAdapter view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            TYPE_PRODUCT -> (holder as OrderProductVH).bind(getItem(position) as Product)
            TYPE_FOOTER -> (holder as OrderFooterVH).bind(
                getItem(position) as OrderFooter,
                deleteOrder
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Product -> TYPE_PRODUCT
            is OrderFooter -> TYPE_FOOTER
        }
    }

    class OrderProductVH(
        private val binding: RvOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            Glide.with(binding.root)
                .load(product.imageUri)
                .placeholder(R.drawable.loader_anim)
                .error(R.drawable.ic_load_error)
                .into(binding.image)
            binding.apply {
                title.text = product.nameProduct.parseAsHtml()
                count.text = "${product.count}"
                price.text = "${moneyFormat.format(product.price)} \u20bd"
                cost.text = "${moneyFormat.format(product.price * product.count)} \u20bd"
                if (product.prizeCost != 0) container.visibility = View.GONE
                else container.visibility = View.VISIBLE
            }

        }

        companion object {

            fun getViewHolder(parent: ViewGroup): OrderProductVH {
                val binding = RvOrderProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderProductVH(binding)
            }

        }

    }

    class OrderFooterVH(
        private val binding: RvOrderFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            footer: OrderFooter,
            deleteOrder: (Long) -> Unit
        ) {
            binding.apply {
                count.text = footer.count.toString()
                discount.text = "${moneyFormat.format(footer.discount)} %"
                discountSum.text = "${moneyFormat.format(footer.discountSum)} \u20bd"
                delivery.text = "${moneyFormat.format(footer.deliveryCost)} \u20bd"
                total.text = "${moneyFormat.format(footer.totalCost)} \u20bd"
                delete.setOnClickListener {
                    deleteOrder.invoke(footer.orderId)
                }
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): OrderFooterVH {
                val binding = RvOrderFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderFooterVH(binding)
            }

        }

    }

    class OrderItemDiffUtilCallback : DiffUtil.ItemCallback<OrderItem>() {

        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return false
        }
    }

    companion object {

        const val TYPE_PRODUCT = 0
        const val TYPE_FOOTER = 1

    }

}