package ru.hvost.news.data.api.response

data class CartResponse(
    val result: String?,
    val error: String?,
    val isPrizes: Boolean?,
    val totalSum: Float?,
    val olSum: Float?,
    val discount: Float?,
    val discountSum: Float?,
    val deliveryCost: Float?,
    val products: List<Product>?
){
    data class Product(
        val productId: Long?,
        val count: Int?,
        val title: String?,
        val imageUrl: String?,
        val price: Float?,
        val isForBonuses: Boolean?,
        val bonusPrice: Float?
    )
}