package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.PetToysResponse

data class PetToys(
    val id: Long,
    val toyId: String,
    val name: String
)

fun List<PetToysResponse.Toy>.toToys(): List<PetToys> {
    val result = mutableListOf<PetToys>()
    for ((index, toy) in this.withIndex()) {
        result.add(
            PetToys(
                id = index.inc().toLong(),
                toyId = toy.toyId ?: "",
                name = toy.name ?: ""
            )
        )
    }
    result.add(
        0, PetToys(
            id = MainViewModel.UNSELECTED_ID,
            toyId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetToys(
            id = MainViewModel.OTHER_ID,
            toyId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}