package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.utils.emptyImageUri
import kotlin.random.Random

data class CartProduct(
    val id: Long,
    val productId: Long,
    val isForBonuses: Boolean,
    val price: Float,
    val bonusPrice: Int,
    val count: Int,
    val title: String,
    val imageUri: Uri
){

    companion object {

        fun getTestCartProducts(): List<CartProduct> {
            val result = mutableListOf<CartProduct>()
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
            return result
        }

        fun getTestCartPrizes(): List<CartProduct> {
            val result = mutableListOf<CartProduct>()
            for(i in 1L..10L) {
                result.add(
                    CartProduct(
                        id = i,
                        productId = i,
                        isForBonuses = true,
                        price = 0f,
                        bonusPrice = 150,
                        count = Random.nextInt(1,10),
                        title = "Приз для стерилизованных взрослых кошек",
                        imageUri = emptyImageUri
                    )
                )
            }
            return result
        }

    }

}