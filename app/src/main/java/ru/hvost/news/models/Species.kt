package ru.hvost.news.models

import ru.hvost.news.data.api.response.SpeciesResponse

data class Species(
    val id: Long,
    val speciesId: Int,
    val speciesName: String
)

fun List<SpeciesResponse.Species>.toSpecies(): List<Species> {
    val result = mutableListOf<Species>()
    for ((index, specie) in this.withIndex()) {
        result.add(
            Species(
                id = index.toLong(),
                speciesId = specie.id?.toInt() ?: 0,
                speciesName = specie.name ?: ""
            )
        )
    }
    return result
}