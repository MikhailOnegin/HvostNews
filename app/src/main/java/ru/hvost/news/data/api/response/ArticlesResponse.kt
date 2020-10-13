package ru.hvost.news.data.api.response

data class ArticlesResponse(
    val result: String?,
    val articles: List<Article>?
) {
    data class Article(
        val domainId: String?,
        val domainTitle: String?,
        val domainIcon: String?,
        val categoryId: String?,
        val categoryTitle: String?,
        val title: String?,
        val imageUrl: String?,
        val shortDescription: String?,
        val viewsCount: String?,
        val likesCount: Int?
    )
}