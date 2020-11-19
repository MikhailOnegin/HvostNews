package ru.hvost.news.data.api.response

data class GetOrdersResponse(
    val result: String?,
    val error: String?,
    val orders: List<Order>?
) {

    data class Order(
        val orderId: Long?,
        val orderDate: String?,
        val orderStatus: String?,
        val discountPercent: Float?,
        val discountCurrency: Float?,
        val deliveryCost: Float?,
        val totalCost: Float?,
        val products: List<Product>?,
    )

    data class Product(
        val productId: Long?,
        val nameProduct: String?,
        val count: Int?,
        val price: Float?,
        val imageUrl: String?
    )

}