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
    val viewsCount: String,
    val shareLink: String,
    val likesCount: Int
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
                viewsCount = article.viewsCount ?: "",
                shareLink = article.articleUrl ?: "",
                likesCount = article.likesCount ?: 0
            )
        )
    }
    return result
}