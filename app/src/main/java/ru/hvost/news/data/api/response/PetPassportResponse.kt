package ru.hvost.news.data.api.response

data class PetPassportResponse(
    val result: String?,
    val error: String?,
    val isSterilised: Boolean?,
    val vacineTitle: String?,
    val vacinationDate: String?,
    val dewormingTitle: String?,
    val dewormingDate: String?,
    val exoparasiteTitle: String?,
    val exoparasitesDate: String?,
    val feeding: String?,
    val diseases: List<Diseases>,
    val favouriteVetName: String?,
    val favouriteVetAdress: String?
)

data class Diseases(
    val id: String?,
    val diseaseName: String?
)