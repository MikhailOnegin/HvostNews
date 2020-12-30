package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.response.CartResponse
import ru.hvost.news.utils.getUriForBackendImagePath

sealed class CartItem

data class CartProduct(
    val id: Long,
    val productId: Long,
    val isForBonuses: Boolean,
    val price: Float,
    val bonusPrice: Float,
    val count: Int,
    val title: String,
    val imageUri: Uri
) : CartItem()

data class CartFooter(
    val totalCost: Float,
    val oldCost: Float,
    val bonusesCost: Float,
    val discount: Float,
    val discountSum: Float,
    val isForPrizes: Boolean,
    val deliveryCost: Float
) : CartItem()

fun CartResponse.toCartItems(): List<CartItem> {
    val cartItems = mutableListOf<CartItem>()
    products?.run {
        for((index, product) in this.withIndex()) {
            cartItems.add(
                CartProduct(
                    id = index.toLong(),
                    productId = product.productId ?: 0L,
                    isForBonuses = product.isForBonuses ?: false,
                    price = product.price ?: 0f,
                    bonusPrice = product.bonusPrice ?: 0f,
                    count = product.count ?: 0,
                    title = product.title ?: App.getInstance().getString(R.string.stub),
                    imageUri = getUriForBackendImagePath(product.imageUrl)
                )
            )
        }
    }
    if (!products.isNullOrEmpty()) {
        cartItems.add(
            CartFooter(
                totalCost = totalSum ?: 0f,
                oldCost = olSum ?: 0f,
                bonusesCost = totalSum ?: 0f,
                isForPrizes = isPrizes ?: false,
                discount = discount ?: 0f,
                discountSum = discountSum ?: 0f,
                deliveryCost = deliveryCost ?: 0f
            )
        )
    }
    return cartItems
}