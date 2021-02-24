package ru.hvost.news.data.api.response

data class PetFeedResponse(
    val result: String?,
    val error: String?,
    val feeds: List<Feed>?
) {
    data class Feed(
        val feedId: String?,
        val name: String?
    )
}