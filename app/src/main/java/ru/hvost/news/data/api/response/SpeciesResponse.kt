package ru.hvost.news.data.api.response

data class SpeciesResponse(
    val result: String?,
    val error: String?,
    val species: List<Species>?
){
    data class Species(
        val id: String?,
        val name: String?
    )
}