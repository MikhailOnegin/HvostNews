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
        val petFeed: List<PetFeed>?,
        val petDelicies: List<Delicies>?,
        val petToy: List<Toy>?,
        val petBadHabbit: List<BadHabbit>?,
        val petChip: String?,
        val isPetForShows: Boolean?,
        val petSports: List<PetSports>?,
        val hasTitles: Boolean?,
        val visitsSaloons: Boolean?,
        val petEducation: List<Education>?
    ) {
        data class Delicies(
            val delliciousId: String?,
            val name: String?
        )

        data class PetFeed(
            val feedId: String?,
            val name: String?
        )

        data class BadHabbit(
            val habbitId: String?,
            val name: String?
        )

        data class PetSports(
            val sportId: String?,
            val name: String?
        )

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