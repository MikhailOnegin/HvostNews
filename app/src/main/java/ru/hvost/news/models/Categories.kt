package ru.hvost.news.models

data class Categories(
    val id: Long,
    val domain: Long,
    val title: String
)

fun List<Article>.toCategory(): List<Categories> {
    val map = mutableMapOf<String, Categories>()
    for (article in this) {
        if (!map.containsKey(article.categoryId)) {
            map[article.categoryId] = Categories(
                id = article.categoryId.toLong(),
                domain = article.domainId.toLong(),
                title = article.categoryTitle
            )
        }
    }
    return map.map { it.value }
}