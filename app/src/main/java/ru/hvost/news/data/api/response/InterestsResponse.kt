package ru.hvost.news.data.api.response

data class InterestsResponse(
    val result: String?,
    val interests: List<Interest>?
) {
    data class Interest(
        val id: String?,
        val name: String?,
        val imageUrl: String?,
        val categoryId: String?,
        val categoryName: String?
    )
}
