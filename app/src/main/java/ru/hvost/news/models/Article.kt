package ru.hvost.news.models

import ru.hvost.news.data.api.response.ArticlesResponse

data class Article(
    val id: Long,
    val domainId: String,
    val domainTitle: String,
    val domainIcon: String,
    val categoryId: String,
    val categoryTitle: String,
    val title: String,
    val imageUrl: String,
    val shortDescription: String,
    val content: String,
    var viewsCount: Int,
    val shareLink: String,
    var likesCount: Int,
    val articleId: String,
    val articleUrl: String,
    var isLiked: Boolean
)

fun List<ArticlesResponse.Article>.toArticles(): List<Article> {
    val result = mutableListOf<Article>()
    for ((index, article) in this.withIndex()) {
        result.add(
            Article(
                id = index.toLong(),
                domainId = article.domainId ?: "",
                domainTitle = article.domainTitle ?: "",
                domainIcon = article.domainIcon ?: "",
                categoryId = article.categoryId ?: "",
                categoryTitle = article.categoryTitle ?: "",
                title = article.title ?: "",
                imageUrl = article.imageUrl ?: "",
                shortDescription = article.shortDescription ?: "",
                content = article.content ?: "",
                viewsCount = article.viewsCount ?: 0,
                shareLink = article.articleUrl ?: "",
                likesCount = article.likesCount ?: 0,
                articleId = article.articleId ?: "",
                articleUrl = article.articleUrl ?: "",
                isLiked = article.isLiked ?: false
            )
        )
    }
    return result
}