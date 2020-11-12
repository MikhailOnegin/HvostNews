package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.response.CartResponse
import ru.hvost.news.utils.emptyImageUri
import ru.hvost.news.utils.getUriForBackendImagePath
import kotlin.random.Random

sealed class CartItem {

    companion object {

        fun getTestProductCart(): List<CartItem> {
            val result = mutableListOf<CartItem>()
            for(i in 1L..10L) {
                result.add(
                    CartProduct(
                        id = i,
                        productId = i,
                        isForBonuses = false,
                        price = 12650.45f,
                        bonusPrice = 0,
                        count = Random.nextInt(1,10),
                        title = "CANAGAN Grain Free, Scottish Salmon, корм 6 кг для собак всех возрастов и щенков, Шотландский. CANAGAN Grain Free, Scottish Salmon, корм 6 кг для собак всех возрастов и щенков, Шотландский.",
                        imageUri = emptyImageUri
                    )
                )
            }
            result.add(CartFooter(
                totalCost = 12650f,
                oldCost = 23800f,
                bonusesCost = 0,
                isForPrizes = false,
                discountSum = 200f,
                discount = 0.2f,
                deliveryCost = 300f
            ))
            return result
        }

        fun getTestPrizesCart(): List<CartItem> {
            val result = mutableListOf<CartItem>()
            result.addAll( listOf(
                CartProduct(
                    id = 1,
                    productId = 1,
                    isForBonuses = true,
                    price = 0f,
                    bonusPrice = 1,
                    count = 1,
                    title = "Приз для стерилизованных взрослых кошек",
                    imageUri = emptyImageUri
                ),
                CartProduct(
                    id = 2,
                    productId = 2,
                    isForBonuses = true,
                    price = 0f,
                    bonusPrice = 3,
                    count = 1,
                    title = "Приз для стерилизованных взрослых кошек",
                    imageUri = emptyImageUri
                ),
                CartProduct(
                    id = 3,
                    productId = 3,
                    isForBonuses = true,
                    price = 0f,
                    bonusPrice = 5,
                    count = 1,
                    title = "Приз для стерилизованных взрослых кошек",
                    imageUri = emptyImageUri
                )
            ))
            result.add(CartFooter(
                totalCost = 0f,
                oldCost = 0f,
                bonusesCost = 222,
                isForPrizes = true,
                discountSum = 200f,
                discount = 0.2f,
                deliveryCost = 300f
            ))
            return result
        }

    }

}

data class CartProduct(
    val id: Long,
    val productId: Long,
    val isForBonuses: Boolean,
    val price: Float,
    val bonusPrice: Int,
    val count: Int,
    val title: String,
    val imageUri: Uri
) : CartItem()

data class CartFooter(
    val totalCost: Float,
    val oldCost: Float,
    val bonusesCost: Int,
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
                    isForBonuses = false, //sergeev: Исправить после доработки на бэке.
                    price = product.price ?: 0f,
                    bonusPrice = 0, //sergeev: Исправить после доработки на бэке.
                    count = product.count ?: 0,
                    title = product.title ?: App.getInstance().getString(R.string.stub),
                    imageUri = getUriForBackendImagePath(product.imageUrl)
                )
            )
        }
    }
    cartItems.add(
        CartFooter(
            totalCost = totalSum ?: 0f,
            oldCost = olSum ?: 0f,
            bonusesCost = 0, //sergeev: Исправить после доработки на бэке.
            isForPrizes = false, //sergeev: Исправить после доработки на бэке.
            discount = discount ?: 0f,
            discountSum = discountSum ?: 0f,
            deliveryCost = deliveryCost ?: 0f
        )
    )
    return cartItems
}