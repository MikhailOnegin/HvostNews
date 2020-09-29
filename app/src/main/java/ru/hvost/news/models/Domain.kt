package ru.hvost.news.models

data class Domain(
    val id: Long,
    val img: String,
    val title: String
)

fun List<Article>.toDomain(): List<Domain> {
    val map = mutableMapOf<String, Domain>()
    for ((i,article) in this.withIndex()){
        if(!map.containsKey(article.domain)){
            map[article.domain] = Domain(
                i.toLong(), article.domainIcon, article.domain
            )
        }
    }
    return map.map { it.value }
}