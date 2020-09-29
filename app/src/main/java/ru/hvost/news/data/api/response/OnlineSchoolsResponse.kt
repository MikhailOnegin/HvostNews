package ru.hvost.news.data.api.response

data class OnlineSchoolsResponse(
    val result: String?,
    val error: String?,
    val schools: List<School>?
) {
    data class School(
        val id: Long?,
        val title: String?,
        val image: String?,
        val userRank: String?,
        val images: String?,
        val description: String?
    )
}