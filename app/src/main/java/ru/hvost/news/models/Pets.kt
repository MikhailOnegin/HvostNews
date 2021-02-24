package ru.hvost.news.models

import ru.hvost.news.data.api.response.PetsResponse

data class Pets(
    val petId: String,
    val petName: String,
    val petSpecies: String,
    val petSex: String,
    val petBreed: String,
    val breedName: String,
    val petBirthday: String,
    val petFeed: List<Feed>,
    val petDelicies: List<Delice>,
    val petToy: List<Toy>,
    val petBadHabbit: List<Habbit>,
    val petChip: String,
    val isPetForShows: Boolean,
    val petSports: List<PetSport>,
    val hasTitles: Boolean,
    val visitsSaloons: Boolean,
    val petEducation: List<PetsEducation>
)

data class Toy(
    val toyId: String,
    val name: String
)

data class PetsEducation(
    val educationId: String?,
    val name: String
)

data class Delice(
    val delliciousId: String?,
    val name: String
)

data class Feed(
    val feedId: String?,
    val name: String
)

data class Habbit(
    val habbitId: String?,
    val name: String
)

data class PetSport(
    val sportId: String?,
    val name: String
)

fun List<PetsResponse.Pet>.toPets(): List<Pets> {
    val result = mutableListOf<Pets>()
    for (pet in this) {
        result.add(
            Pets(
                petId = pet.petId ?: "",
                petName = pet.petName ?: "",
                petSpecies = pet.petSpecies ?: "",
                petSex = pet.petSex ?: "",
                petBreed = pet.petBreed ?: "",
                breedName = pet.breedName ?: "",
                petBirthday = pet.petBirthday ?: "",
                petDelicies = pet.petDelicies?.map {
                    Delice(
                        delliciousId = it.delliciousId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                petToy = pet.petToy?.map {
                    Toy(
                        toyId = it.toyId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                petBadHabbit = pet.petBadHabbit?.map {
                    Habbit(
                        habbitId = it.habbitId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                petChip = pet.petChip ?: "",
                isPetForShows = pet.isPetForShows ?: false,
                petSports = pet.petSports?.map {
                    PetSport(
                        sportId = it.sportId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                hasTitles = pet.hasTitles ?: false,
                visitsSaloons = pet.visitsSaloons ?: false,
                petEducation = pet.petEducation?.map {
                    PetsEducation(
                        educationId = it.educationId,
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                petFeed = pet.petFeed?.map {
                    Feed(
                        feedId = it.feedId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf()
            )
        )
    }
    return result
}