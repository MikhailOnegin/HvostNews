package ru.hvost.news.data.api.response

data class PetDeliciesResponse(
    val result: String?,
    val error: String?,
    val delicies: List<Delicies>?
) {
    data class Delicies(
        val delliciousId: String?,
        val name: String?
    )
}