package ru.hvost.news.data.api.response

data class PreparationsResponse(
    val result: String?,
    val preparations: List<Preparations>,
    val error: String?
)

data class Preparations(
    val id: String?,
    val name: String?,
    val typeId: String?
)