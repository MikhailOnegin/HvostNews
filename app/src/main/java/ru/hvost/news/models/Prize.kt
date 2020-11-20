package ru.hvost.news.models

import ru.hvost.news.data.api.response.PrizesResponse

data class Prize(
    val prizeId: String,
    val prizeCost: String,
    val category: String,
    val products: List<Product>,
) {
    data class Product(
        val id: Long,
        val title: String,
        val count: Int,
        val imageUrl: String
    )
}

fun List<PrizesResponse.Prize>.toPrize(): List<Prize> {
    val result = mutableListOf<Prize>()
    for (prize in this) {
        result.add(
            Prize(
                prizeId = prize.prizeId ?: "",
                prizeCost = prize.prizeCost ?: "",
                category = prize.category ?: "",
                products = prize.products?.mapIndexed { index, product ->
                    Prize.Product(
                        id = index.inc().toLong(),
                        title = product.title ?: "",
                        count = product.count ?: 0,
                        imageUrl = product.imageUrl ?: ""
                    )
                } ?: listOf()
            )
        )
    }
    result.sortBy { it.prizeCost.toInt() }
    return result
}