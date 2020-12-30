package ru.hvost.news.models

import ru.hvost.news.data.api.response.BreedsResponse

data class Breeds(
    val id: Long,
    val breedId: String,
    val breedName: String
)

fun List<BreedsResponse.Breed>.toBreeds(): List<Breeds> {
    val result = mutableListOf<Breeds>()
    for ((index, breed) in this.withIndex()) {
        result.add(
            Breeds(
                id = index.toLong(),
                breedId = breed.id ?: "",
                breedName = breed.name ?: ""
            )
        )
    }
    return result
}