package ru.hvost.news.data.api.response

data class PetToysResponse(
    val result: String?,
    val error: String?,
    val toys: List<Toy>?
) {
    data class Toy(
        val toyId: String?,
        val name: String?
    )
}