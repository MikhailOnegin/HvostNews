package ru.hvost.news.data.api.response

data class PetEducationResponse(
    val result: String?,
    val error: String?,
    val educations: List<PetsEducation>?
) {
    data class PetsEducation(
        val educationId: String?,
        val name: String?
    )
}