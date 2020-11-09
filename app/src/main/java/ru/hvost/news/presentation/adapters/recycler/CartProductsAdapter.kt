package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.RvCartFooterBinding
import ru.hvost.news.databinding.RvCartPrizeBinding
import ru.hvost.news.databinding.RvCartProductBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.CartProduct
import ru.hvost.news.utils.WordEnding
import ru.hvost.news.utils.getWordEndingType
import ru.hvost.news.utils.moneyFormat
import ru.hvost.news.utils.showNotReadyToast
import java.lang.IllegalArgumentException

class CartProductsAdapter
    : ListAdapter<CartItem, RecyclerView.ViewHolder>(CartProductDiffUtilItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_PRODUCT -> CartProductVH.getViewHolder(parent)
            TYPE_PRIZE -> CartPrizeVH.getViewHolder(parent)
            TYPE_FOOTER -> CartFooterVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong adapter view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            TYPE_PRODUCT -> {
                (holder as CartProductVH).bind(getItem(position) as CartProduct, position, itemCount)
            }
            TYPE_PRIZE -> {
                (holder as CartPrizeVH).bind(getItem(position) as CartProduct, position, itemCount)
            }
            TYPE_FOOTER -> {
                (holder as CartFooterVH).bind(getItem(position) as CartFooter)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is CartProduct -> {
                val product = getItem(position) as CartProduct
                if(product.isForBonuses) TYPE_PRIZE else TYPE_PRODUCT
            }
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
                minus.setOnClickListener {
                    //sergeev: Удаление товара.
                    showNotReadyToast()
                }
                plus.setOnClickListener {
                    //sergeev: Добавление товара.
                    showNotReadyToast()
                }
                binding.remove.setOnClickListener {
                    //sergeev: Удаление товара.
                    showNotReadyToast()
                }
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

    class CartPrizeVH(
        val binding: RvCartPrizeBinding
    ): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartProduct, position: Int, size: Int) {
            Glide.with(binding.root).load(item.imageUri).into(binding.image)
            binding.run {
                title.text = item.title
                val forPrep = App.getInstance().getString(R.string.cartForPrep)
                val bonuses = when(getWordEndingType(item.bonusPrice)){
                    WordEnding.TYPE_1 -> App.getInstance().getString(R.string.cartForBonusesType1)
                    WordEnding.TYPE_2 -> App.getInstance().getString(R.string.cartForBonusesType2)
                    WordEnding.TYPE_3 -> App.getInstance().getString(R.string.cartForBonusesType3)
                }
                binding.bonuses.text = "$forPrep ${item.bonusPrice} $bonuses"
                if(position == size - 2) divider.visibility = View.GONE
                else divider.visibility = View.VISIBLE
                binding.remove.setOnClickListener {
                    //sergeev: Удаление товара.
                    showNotReadyToast()
                }
                binding.root.setOnClickListener {
                    //sergeev: Товары в наборе.
                    showNotReadyToast()
                }
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): CartPrizeVH {
                val binding = RvCartPrizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CartPrizeVH(binding)
            }

        }

    }

    class CartFooterVH(
        val binding: RvCartFooterBinding
    ): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartFooter) {
            binding.run {
                if(item.isForPrizes){
                    val bonuses = when(getWordEndingType(item.bonusesCost)){
                        WordEnding.TYPE_1 -> App.getInstance().getString(R.string.cartForBonusesType1)
                        WordEnding.TYPE_2 -> App.getInstance().getString(R.string.cartForBonusesType2)
                        WordEnding.TYPE_3 -> App.getInstance().getString(R.string.cartForBonusesType3)
                    }
                    total.text = "${item.bonusesCost} $bonuses"
                    oldPrice.visibility = View.GONE
                    oldPriceTitle.visibility = View.GONE
                }else{
                    total.text = "${moneyFormat.format(item.totalCost.toInt())} \u20bd"
                    oldPrice.visibility = View.VISIBLE
                    oldPriceTitle.visibility = View.VISIBLE
                }
                oldPrice.text = "${moneyFormat.format(item.oldCost.toInt())} \u20bd"
                binding.makeOrder.setOnClickListener {
                    //sergeev: Переход к оформлению заказа.
                    showNotReadyToast()
                }
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
        const val TYPE_PRIZE = 2
        const val TYPE_FOOTER = 3

    }

}