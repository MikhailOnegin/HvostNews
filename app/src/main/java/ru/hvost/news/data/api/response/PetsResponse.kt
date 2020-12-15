package ru.hvost.news.data.api.response

data class PetsResponse(
    val result: String?,
    val error: String?,
    val pets: List<Pet>?
) {
    data class Pet(
        val petId: String?,
        val petName: String?,
        val isArchived: Boolean?,
        val petSpecies: String?,
        val petSex: String?,
        val petBreed: String?,
        val breedName: String?,
        val petBirthday: String?,
        val petDelicies: String?,
        val petToy: List<Toy>?,
        val petBadHabbit: String?,
        val petChip: String?,
        val isPetForShows: Boolean?,
        val isSportsPet: Boolean?,
        val hasTitles: Boolean?,
        val visitsSaloons: Boolean?,
        val petEducation: List<Education>?
    ){
        data class Toy(
            val toyId: String?,
            val name: String?
        )

        data class Education(
            val educationId: String?,
            val name: String?
        )
    }
}