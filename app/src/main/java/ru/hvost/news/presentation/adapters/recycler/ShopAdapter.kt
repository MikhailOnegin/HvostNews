package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.*
import ru.hvost.news.models.*
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class ShopAdapter(
    private val fullList: List<ShopItem>
) : ListAdapter<ShopItem, RecyclerView.ViewHolder>(ShopItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_CATEGORY -> CategoryVH.getViewHolder(parent, this)
            TYPE_HEADER -> HeaderVH.getViewHolder(parent)
            TYPE_PRODUCT -> ProductVH.getViewHolder(parent)
            TYPE_MESSAGE -> MessageVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong shop adapter view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is ShopCategory -> (holder as CategoryVH).bind(item, position)
            is ShopHeader -> (holder as HeaderVH).bind(item)
            is ShopProduct -> (holder as ProductVH).bind(item)
            is ShopMessage -> (holder as MessageVH).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is ShopCategory -> TYPE_CATEGORY
            is ShopHeader -> TYPE_HEADER
            is ShopProduct -> TYPE_PRODUCT
            is ShopMessage -> TYPE_MESSAGE
        }
    }

    fun handleClickOnCategory(position: Int) {
        val category = getItem(position) as ShopCategory
        category.isExpanded = !category.isExpanded
        val newList = if(category.isExpanded) {
            val list = currentList.toMutableList()
            list.addAll(
                position + 1,
                fullList.filter { it.categoryId == category.id }
            )
            list
        } else {
            currentList.filter { it.categoryId != category.id }
        }
        submitList(newList)
    }

    class CategoryVH(
        private val binding: RvShopCategoryBinding,
        private val adapter: ShopAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ShopCategory, position: Int) {
            binding.apply {
                text.text = category.name
                root.setOnClickListener {
                    adapter.handleClickOnCategory(position)
                }
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup, adapter: ShopAdapter): CategoryVH {
                val binding = RvShopCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryVH(binding, adapter)
            }

        }

    }

    class HeaderVH(
        private val binding: RvShopHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: ShopHeader) {
            binding.run {
                Glide.with(binding.root).load(header.imageUri).into(image)
                title.text = header.text
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): HeaderVH {
                val binding = RvShopHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return HeaderVH(binding)
            }

        }

    }

    class ProductVH(
        private val binding: RvShopProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ShopProduct) {
            binding.apply {
                Glide.with(binding.root).load(product.imageUri).into(image)
                title.text = product.description.parseAsHtml()
                price.text = "${moneyFormat.format(product.price.toInt())} \u20bd"
                oldPrice.text = "${moneyFormat.format(product.oldPrice.toInt())} \u20bd"
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ProductVH {
                val binding = RvShopProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProductVH(binding)
            }

        }

    }

    class MessageVH(
        private val binding: RvShopMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ShopMessage) {
            binding.apply {
                text.text = message.message
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): MessageVH {
                val binding = RvShopMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MessageVH(binding)
            }

        }

    }

    class ShopItemDiffUtilCallback : DiffUtil.ItemCallback<ShopItem>() {

        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return false
        }
    }

    companion object {

        const val TYPE_CATEGORY = 1
        const val TYPE_HEADER = 2
        const val TYPE_PRODUCT = 3
        const val TYPE_MESSAGE = 4

    }

}