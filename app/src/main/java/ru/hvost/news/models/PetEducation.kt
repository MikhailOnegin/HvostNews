package ru.hvost.news.models

import ru.hvost.news.data.api.response.PetEducationResponse

data class PetEducation(
    val id: Long,
    val educationId: String,
    val name: String
)

fun List<PetEducationResponse.PetsEducation>.toEducation(): List<PetEducation> {
    val result = mutableListOf<PetEducation>()
    for ((index, education) in this.withIndex()) {
        result.add(
            PetEducation(
                id = index.toLong(),
                educationId = education.educationId ?: "",
                name = education.name ?: ""
            )
        )
    }
    return result
}