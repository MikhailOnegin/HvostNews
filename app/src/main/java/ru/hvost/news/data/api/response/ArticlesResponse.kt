package ru.hvost.news.data.api.response

data class ArticlesResponse(
    val result: String?,
    val error: String?,
    val articles: List<Article>?
) {
    data class Article(
        val articleId: String?,
        val domainId: String?,
        val domainTitle: String?,
        val domainIcon: String?,
        val categoryId: String?,
        val categoryTitle: String?,
        val title: String?,
        val imageUrl: String?,
        val shortDescription: String?,
        val description: String?,
        val viewsCount: Int?,
        val articleUrl: String?,
        val likesCount: Int?
    )
}