package ru.hvost.news.models

import ru.hvost.news.data.api.response.BreedsResponse

data class Breeds(
    val breedId: String,
    val breedName: String
)

fun List<BreedsResponse.Breed>.toBreeds(): List<Breeds> {
    val result = mutableListOf<Breeds>()
    for (breed in this) {
        result.add(
            Breeds(
                breedId = breed.id ?: "",
                breedName = breed.name ?: ""
            )
        )
    }
    return result
}