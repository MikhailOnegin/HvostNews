package ru.hvost.news.data.api.response

data class PrizesResponse(
    val result: String?,
    val error: String?,
    val prizes: List<Prize>?
) {
    data class  Prize(
        val prizeId: String?,
        val prizeCost: String?,
        val category: String?,
        val products: List<Product>?
    ) {
        data class Product(
            val title: String?,
            val count: Int?,
            val imageUrl: String?
        )
    }
}