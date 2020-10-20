package ru.hvost.news.data.api.response

data class PetsResponse(
    val result: String?,
    val pets: List<Pet>?
) {
    data class Pet(
        val petId: String?,
        val petName: String?,
        val petSpecies: String?,
        val petSex: String?,
        val petBreed: String?,
        val petBirthday: String?,
        val petDelicies: String?,
        val petToy: String?,
        val petBadHabbit: String?,
        val petChip: String?,
        val isPetForShows: Boolean?,
        val isSportsPet: Boolean?,
        val hasTitles: Boolean?,
        val visitsSaloons: Boolean?,
        val petEducation: String?
    )
}