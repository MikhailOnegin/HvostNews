package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.PetFeedResponse

data class PetFeed(
    val index: Long,
    val feedId: String,
    val name: String
)

fun List<PetFeedResponse.Feed>.toFeed(): List<PetFeed> {
    val result = mutableListOf<PetFeed>()
    for ((index, feed) in this.withIndex()) {
        result.add(
            PetFeed(
                index = index.inc().toLong(),
                feedId = feed.feedId ?: "",
                name = feed.name ?: "",
            )
        )
    }
    result.add(
        0, PetFeed(
            index = MainViewModel.UNSELECTED_ID,
            feedId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetFeed(
            index = MainViewModel.OTHER_ID,
            feedId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}