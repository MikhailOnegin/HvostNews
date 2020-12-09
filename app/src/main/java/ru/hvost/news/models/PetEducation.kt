package ru.hvost.news.models

import ru.hvost.news.data.api.response.PetEducationResponse

data class PetEducation(
    val toyId: String,
    val name: String
)

fun List<PetEducationResponse.PetsEducation>.toEducation(): List<PetEducation> {
    val result = mutableListOf<PetEducation>()
    for (education in this) {
        result.add(
            PetEducation(
                toyId = education.educationId ?: "",
                name = education.name ?: ""
            )
        )
    }
    return result
}