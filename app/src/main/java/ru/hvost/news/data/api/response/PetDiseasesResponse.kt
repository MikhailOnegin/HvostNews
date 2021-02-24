package ru.hvost.news.data.api.response

data class PetDiseasesResponse(
    val result: String?,
    val error: String?,
    val diseases: List<Disease>?
) {
    data class Disease(
        val diseaseId: String?,
        val name: String?
    )
}