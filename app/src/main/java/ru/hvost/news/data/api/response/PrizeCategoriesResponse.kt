package ru.hvost.news.data.api.response

data class PrizeCategoriesResponse(
    val result: String?,
    val error: String?,
    val categories: List<Category>?
) {
    data class Category(
        val categoryId: String?,
        val name: String?
    )
}