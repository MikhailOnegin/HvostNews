package ru.hvost.news.data.api.response

data class ArticlesResponse(
    val result: String?,
    val error: String?,
    val articles: List<Article>?,
    val article: Article?
) {
    data class Article(
        val articleId: String?,
        val domainId: String?,
        val domainTitle: String?,
        val domainIcon: String?,
        val categoryId: String?,
        val categoryTitle: String?,
        val title: String?,
        val publicationDate: String?,
        val imageUrl: String?,
        val shortDescription: String?,
        val description: String?,
        val viewsCount: Int?,
        val articleUrl: String?,
        val likesCount: Int?,
        val content: String?,
        val postTypeId: String?,
        val postTypeName: String?,
        val isLiked: Boolean?
    )
}