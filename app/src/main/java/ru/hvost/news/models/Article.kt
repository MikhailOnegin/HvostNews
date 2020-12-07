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
    val description: String,
    val viewsCount: Int,
    val shareLink: String,
    val likesCount: Int,
    val articleId: String,
    val articleUrl: String,
    val isLiked: Boolean = false
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
                description = article.description ?: "",
                viewsCount = article.viewsCount ?: 0,
                shareLink = article.articleUrl ?: "",
                likesCount = article.likesCount ?: 0,
                articleId = article.articleId ?: "",
                articleUrl = article.articleUrl ?: ""
            )
        )
    }
    return result
}