package ru.hvost.news.models

data class Categories(
    val id: Long,
    val title: String
)

fun List<Article>.toCategory(): List<Categories> {
    val map = mutableMapOf<String, Categories>()
    for (article in this) {
        if (!map.containsKey(article.domainTitle)) {
            map[article.domainTitle] = Categories(
                id = article.categoryId.toLong(),
                title = article.categoryTitle
            )
        }
    }
    return map.map { it.value }
}