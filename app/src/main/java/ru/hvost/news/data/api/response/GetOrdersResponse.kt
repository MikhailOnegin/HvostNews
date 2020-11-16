package ru.hvost.news.data.api.response

data class GetOrdersResponse(
    val result: String?,
    val error: String?,
    val orders: Map<String, Order>?
) {

    data class Order(
        val orderId: String?,
        val orderDate: String?,
        val orderStatus: String?,
        val discountPercent: Float?,
        val discountCurrency: String?,
        val deliveryCost: String?,
        val totalCost: String?,
        val products: List<Product>?,
    )

    data class Product(
        val productId: String?,
        val nameProduct: String?,
        val count: String?,
        val price: String?
    )

}