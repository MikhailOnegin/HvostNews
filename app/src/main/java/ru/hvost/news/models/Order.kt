package ru.hvost.news.models

import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.response.GetOrdersResponse
import ru.hvost.news.utils.ordersStatuses

data class Order(
    val id: Long,
    val orderId: Long,
    val orderDate: String,
    val orderStatus: String,
    val discountPercent: Float,
    val discountCurrency: Float,
    val deliveryCost: Float,
    val totalCost: Float,
    val products: List<Product>,
) {

    data class Product(
        val id: Long,
        val productId: Long,
        val nameProduct: String,
        val count: Int,
        val price: Float
    )

}

fun GetOrdersResponse.toOrders() : List<Order> {
    if(orders == null) return listOf()
    orders.run {
        val result = mutableListOf<Order>()
        for((orderIndex,responseOrder) in this.withIndex()) {
            val products = mutableListOf<Order.Product>()
            for((productIndex, responseProduct) in responseOrder.products?.withIndex() ?: listOf()) {
                products.add(
                    Order.Product(
                        id = productIndex.toLong(),
                        productId = responseProduct.productId ?: 0L,
                        nameProduct = responseProduct.nameProduct ?: "",
                        count = responseProduct.count ?: 0,
                        price = responseProduct.price ?: 0f
                    )
                )
            }
            result.add(
                Order(
                    id = orderIndex.toLong(),
                    orderId = responseOrder.orderId ?: 0L,
                    orderDate = responseOrder.orderDate ?: "",
                    orderStatus = responseOrder.orderStatus ?: "",
                    discountPercent = responseOrder.discountPercent ?: 0f,
                    discountCurrency = responseOrder.discountCurrency ?: 0f,
                    deliveryCost = responseOrder.deliveryCost ?: 0f,
                    totalCost = responseOrder.totalCost ?: 0f,
                    products = products
                )
            )
        }
        return result
    }
}

val String.orderStatus
    get() = ordersStatuses[this] ?: App.getInstance().getString(R.string.stub)