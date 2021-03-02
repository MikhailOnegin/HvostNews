package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.Food
import ru.hvost.news.data.api.response.PetBadHabbitsResponse

data class PetBadHabbits(
    val index: Long,
    val habbitId: String,
    val name: String
)

fun List<PetBadHabbitsResponse.BadHabbit>.toBadHabbits(): List<PetBadHabbits> {
    val result = mutableListOf<PetBadHabbits>()
    for ((index, badHabbit) in this.withIndex()) {
        result.add(
            PetBadHabbits(
                index = index.inc().toLong(),
                habbitId = badHabbit.habbitId ?: "",
                name = badHabbit.name ?: "",
            )
        )
    }
    result.add(
        0, PetBadHabbits(
            index = MainViewModel.UNSELECTED_ID,
            habbitId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetBadHabbits(
            index = MainViewModel.OTHER_ID,
            habbitId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}