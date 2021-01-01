package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.*
import ru.hvost.news.models.*
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class ShopAdapter(
    private val onClick: (Long) -> Unit,
    private val onGoToMapClicked: () -> Unit
) : ListAdapter<ShopItem, RecyclerView.ViewHolder>(ShopItemDiffUtilCallback()) {

    private var fullList: List<ShopItem>? = null

    fun setFullList(fullList: List<ShopItem>) {
        this.fullList = fullList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_CATEGORY -> CategoryVH.getViewHolder(parent, this)
            TYPE_HEADER -> HeaderVH.getViewHolder(parent)
            TYPE_PRODUCT -> ProductVH.getViewHolder(parent, onClick)
            TYPE_MESSAGE -> MessageVH.getViewHolder(parent, this)
            else -> throw IllegalArgumentException("Wrong shop adapter view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is ShopCategory -> (holder as CategoryVH).bind(item, position)
            is ShopHeader -> (holder as HeaderVH).bind(item)
            is ShopProduct -> (holder as ProductVH).bind(item)
            is ShopMessage -> (holder as MessageVH).bind(item, onGoToMapClicked)
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
                fullList?.filter { it.categoryId == category.id } ?: listOf()
            )
            list
        } else {
            currentList.filter { it.categoryId != category.id }
        }
        submitList(newList)
    }

    fun submitList(list: List<ShopItem>?, isAfterChanging: Boolean = false) {
        when {
            currentList.isNullOrEmpty() -> super.submitList(list)
            isAfterChanging -> {
                val newList = list?.filter { new ->
                    currentList.firstOrNull {  old ->
                        new.id == old.id
                    } != null
                }
                super.submitList(newList)
            }
            else -> super.submitList(list)
        }
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
                if(category.selectedProducts != 0) {
                    counter.text = "${category.selectedProducts}"
                    counter.setBackgroundResource(R.drawable.background_shop_circle_colored)
                    counter.visibility = View.VISIBLE
                } else {
                    if (category.isEmpty) {
                        counter.text = ""
                        counter.setBackgroundResource(R.drawable.ic_question)
                        counter.visibility = View.VISIBLE
                    } else counter.visibility = View.GONE
                }
                image.setImageResource(
                    if (category.isExpanded) R.drawable.ic_chevron_up
                    else R.drawable.ic_chevron_down
                )
                val color = if (category.isEmpty)
                    ContextCompat.getColor(App.getInstance(), R.color.gray4)
                else ContextCompat.getColor(App.getInstance(), R.color.gray1)
                image.imageTintList = ColorStateList.valueOf(color)
                text.setTextColor(color)
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
                title.text = header.text.parseAsHtml()
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
        private val binding: RvShopProductBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ShopProduct) {
            binding.apply {
                Glide.with(binding.root).load(product.imageUri).into(image)
                title.text = product.title.parseAsHtml()
                price.text = "${moneyFormat.format(product.price.toInt())} \u20bd"
                oldPrice.text = "${moneyFormat.format(product.oldPrice.toInt())} \u20bd"
                oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                root.setOnClickListener { onClick.invoke(product.id) }
                checked.setImageResource(
                    if(product.isInCart) R.drawable.ic_shop_checked
                    else R.drawable.ic_shop_unchecked
                )
            }
        }

        companion object {

            fun getViewHolder(
                parent: ViewGroup,
                onClick: (Long) -> Unit
            ): ProductVH {
                val binding = RvShopProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProductVH(binding, onClick)
            }

        }

    }

    class MessageVH(
        private val binding: RvShopMessageBinding,
        private val adapter: ShopAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ShopMessage, onGoToMapClicked: () -> Unit) {
            binding.apply {
                text.text = message.message
                close.setOnClickListener {
                    val category = adapter.currentList.firstOrNull { it.id == message.categoryId }
                    val position = adapter.currentList.indexOf(category)
                    adapter.handleClickOnCategory(position)
                }
                button.setOnClickListener { onGoToMapClicked.invoke() }
            }
        }

        companion object {

            fun getViewHolder(
                parent: ViewGroup,
                adapter: ShopAdapter
            ): MessageVH {
                val binding = RvShopMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MessageVH(binding, adapter)
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