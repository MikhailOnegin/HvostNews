package ru.hvost.news.models

import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.response.GetOrdersResponse
import ru.hvost.news.utils.ordersStatuses

data class Order(
    val id: Long,
    val orderId: String,
    val orderDate: String,
    val orderStatus: String,
    val discountPercent: Float,
    val discountCurrency: String,
    val deliveryCost: String,
    val totalCost: String,
    val products: List<Product>,
) {

    data class Product(
        val id: Long,
        val productId: String,
        val nameProduct: String,
        val count: String,
        val price: String
    )

}

fun GetOrdersResponse.toOrders() : List<Order> {
    if(orders == null) return listOf()
    orders.run {
        val result = mutableListOf<Order>()
        for((orderIndex,responseOrder) in values.withIndex()) {
            val products = mutableListOf<Order.Product>()
            for((productIndex, responseProduct) in responseOrder.products?.withIndex() ?: listOf()) {
                products.add(
                    Order.Product(
                        id = productIndex.toLong(),
                        productId = responseProduct.productId ?: "",
                        nameProduct = responseProduct.nameProduct ?: "",
                        count = responseProduct.count ?: "",
                        price = responseProduct.price ?: ""
                    )
                )
            }
            result.add(
                Order(
                    id = orderIndex.toLong(),
                    orderId = responseOrder.orderId ?: "",
                    orderDate = responseOrder.orderDate ?: "",
                    orderStatus = responseOrder.orderStatus ?: "",
                    discountPercent = responseOrder.discountPercent ?: 0f,
                    discountCurrency = responseOrder.discountCurrency ?: "",
                    deliveryCost = responseOrder.deliveryCost ?: "",
                    totalCost = responseOrder.totalCost ?: "",
                    products = products
                )
            )
        }
        return result
    }
}

val String.orderStatus
    get() = ordersStatuses[this] ?: App.getInstance().getString(R.string.stub)