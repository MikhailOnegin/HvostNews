package ru.hvost.news.data.api.response

data class PetPassportResponse(
    val result: String?,
    val petId: String?,
    val petName: String?,
    val isSterilised: Boolean?,
    val vacineId: String?,
    val vacinationDate: String?,
    val vacinationPeriodId: String?,
    val dewormingId: String?,
    val dewormingDate: String?,
    val dewormingPeriodId: String?,
    val exoparasiteId: String?,
    val exoparasiteDate: String?,
    val exoparasitePeriodId: String?,
    val feedingTypeId: String?,
    val diseases: List<String>,
    val favouriteVetName: String?,
    val favouriteVetAdress: String?,
    val error: String?
)