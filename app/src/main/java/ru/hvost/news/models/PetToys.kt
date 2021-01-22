package ru.hvost.news.models

import ru.hvost.news.data.api.response.PetToysResponse

data class Toys(
    val id: Long,
    val toyId: String,
    val name: String
)

fun List<PetToysResponse.Toy>.toToys(): List<Toys> {
    val result = mutableListOf<Toys>()
    for ((index, toy) in this.withIndex()) {
        result.add(
            Toys(
                id = index.inc().toLong(),
                toyId = toy.toyId ?: "",
                name = toy.name ?: ""
            )
        )
    }
    result.add(
        0, Toys(
            id = 0L,
            toyId = "",
            name = "Не выбрано"
        )
    )
    return result
}