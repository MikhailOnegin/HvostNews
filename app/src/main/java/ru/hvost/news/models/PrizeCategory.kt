package ru.hvost.news.models

import ru.hvost.news.data.api.response.PrizeCategoriesResponse

data class PrizeCategory(
    val prizeCategoryId: String,
    val prizeCategoryName: String,
)

fun List<PrizeCategoriesResponse.Category>.toPrizeCategories(): List<PrizeCategory> {
    val result = mutableListOf<PrizeCategory>()
    for (category in this) {
        result.add(
            PrizeCategory(
                prizeCategoryId = category.categoryId ?: "",
                prizeCategoryName = category.name ?: ""
            )
        )
    }
    return result
}