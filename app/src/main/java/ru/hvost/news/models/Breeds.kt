package ru.hvost.news.models

import ru.hvost.news.data.api.response.BreedsResponse

data class Breeds(
    val id: Long,
    val breedId: String?,
    val breedName: String
)

fun List<BreedsResponse.Breed>.toBreeds(): List<Breeds> {
    val result = mutableListOf<Breeds>()
    for ((index, breed) in this.withIndex()) {
        result.add(
            Breeds(
                id = index.toLong().inc(),
                breedId = breed.id,
                breedName = breed.name ?: ""
            )
        )
    }
    if (result.isNotEmpty()) {
        result.add(
            0, Breeds(
                id = 0L,
                breedId = null,
                breedName = "Не выбрано"
            )
        )
    }
    return result
}