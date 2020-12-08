package ru.hvost.news.models

import ru.hvost.news.data.api.response.PetToysResponse


data class Toys(
    val toyId: String,
    val name: String
)

fun List<PetToysResponse.Toy>.toToys(): List<Toys> {
    val result = mutableListOf<Toys>()
    for (toy in this) {
        result.add(
            Toys(
                toyId = toy.toyId ?: "",
                name = toy.name ?: ""
            )
        )
    }
    return result
}