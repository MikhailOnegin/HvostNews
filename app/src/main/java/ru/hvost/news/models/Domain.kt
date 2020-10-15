package ru.hvost.news.models

data class Domain(
    val id: Long,
    val img: String,
    val title: String
)

fun List<Article>.toDomain(): List<Domain> {
    val map = mutableMapOf<String, Domain>()
    for (article in this) {
        if (!map.containsKey(article.domainTitle)) {
            map[article.domainTitle] = Domain(
                id = article.domainId.toLong(),
                img = article.domainIcon,
                title = article.domainTitle
            )
        }
    }
    return map.map { it.value }
}