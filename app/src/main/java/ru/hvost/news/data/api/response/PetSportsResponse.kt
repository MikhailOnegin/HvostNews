package ru.hvost.news.data.api.response

data class PetSportsResponse(
    val result: String?,
    val error: String?,
    val sports: List<Sport>?
) {
    data class Sport(
        val sportId: String?,
        val name: String?
    )
}