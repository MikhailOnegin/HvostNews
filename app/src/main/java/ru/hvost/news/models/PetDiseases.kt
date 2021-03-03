package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.Food
import ru.hvost.news.data.api.response.PetDiseasesResponse

data class PetDiseases(
    val index: Long,
    val diseaseId: String,
    val value: String
)

fun List<PetDiseasesResponse.Disease>.toPetDiseases(): List<PetDiseases> {
    val result = mutableListOf<PetDiseases>()
    for ((index, disease) in this.withIndex()) {
        result.add(
            PetDiseases(
                index = index.inc().toLong(),
                diseaseId = disease.diseaseId ?: "",
                value = disease.value ?: "",
            )
        )
    }
    result.add(
        PetDiseases(
            index = MainViewModel.OTHER_ID,
            diseaseId = "",
            value = MainViewModel.OTHER
        )
    )
    return result
}