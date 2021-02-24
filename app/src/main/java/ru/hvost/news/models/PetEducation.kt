package ru.hvost.news.models

import ru.hvost.news.MainViewModel
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
                id = index.inc().toLong(),
                educationId = education.educationId ?: "",
                name = education.name ?: ""
            )
        )
    }
    result.add(
        0, PetEducation(
            id = MainViewModel.UNSELECTED_ID,
            educationId = "",
            name = MainViewModel.UNSELECTED
        )
    )
    result.add(
        PetEducation(
            id = MainViewModel.OTHER_ID,
            educationId = "",
            name = MainViewModel.OTHER
        )
    )
    return result
}