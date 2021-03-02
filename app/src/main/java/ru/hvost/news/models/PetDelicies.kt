package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.Food
import ru.hvost.news.data.api.response.PetDeliciesResponse

data class PetDelicies(
    val index: Long,
    val delliciousId: String,
    val name: String
)

fun List<PetDeliciesResponse.Delicies>.toPetDelicies(): List<PetDelicies> {
    val result = mutableListOf<PetDelicies>()
    for ((index, petDelicies) in this.withIndex()) {
        result.add(
            PetDelicies(
                index = index.inc().toLong(),
                delliciousId = petDelicies.delliciousId ?: "",
                name = petDelicies.name ?: "",
            )
        )
    }
    result.add(
        0, PetDelicies(
            index = MainViewModel.UNSELECTED_ID,
            delliciousId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetDelicies(
            index = MainViewModel.OTHER_ID,
            delliciousId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}