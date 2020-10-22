package ru.hvost.news.data.api.response

data class BreedsResponse(
    val result: String?,
    val breeds: List<Breed>?
) {
    data class Breed(
        val id: String?,
        val name: String?
    )
}