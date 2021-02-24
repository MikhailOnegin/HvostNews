package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.Food
import ru.hvost.news.data.api.response.PetSportsResponse

data class PetSports(
    val index: Long,
    val sportId: String,
    val name: String
)

fun List<PetSportsResponse.Sport>.toPetSports(): List<PetSports> {
    val result = mutableListOf<PetSports>()
    for ((index, sport) in this.withIndex()) {
        result.add(
            PetSports(
                index = index.inc().toLong(),
                sportId = sport.sportId ?: "",
                name = sport.name ?: "",
            )
        )
    }
    result.add(
        0, PetSports(
            index = MainViewModel.UNSELECTED_ID,
            sportId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetSports(
            index = MainViewModel.OTHER_ID,
            sportId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}