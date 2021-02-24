package ru.hvost.news.data.api.response

data class PetBadHabbitsResponse(
    val result: String?,
    val error: String?,
    val badHabbits: List<BadHabbit>?
) {
    data class BadHabbit(
        val habbitId: String?,
        val name: String?
    )
}