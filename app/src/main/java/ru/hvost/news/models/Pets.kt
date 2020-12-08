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
    val petDelicies: String,
    val petToy: List<Toy>,
    val petBadHabbit: String,
    val petChip: String,
    val isPetForShows: Boolean,
    val isSportsPet: Boolean,
    val hasTitles: Boolean,
    val visitsSaloons: Boolean,
    val petEducation: List<Education>
)

data class Toy(
    val toyId: String,
    val name: String
)

data class Education(
    val educationId: String?,
    val name: String?
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
                petDelicies = pet.petDelicies ?: "",
                petToy = pet.petToy?.map {
                    Toy(
                        toyId = it.toyId ?: "",
                        name = it.name ?: ""
                    )
                } ?: listOf(),
                petBadHabbit = pet.petBadHabbit ?: "",
                petChip = pet.petChip ?: "",
                isPetForShows = pet.isPetForShows ?: false,
                isSportsPet = pet.isSportsPet ?: false,
                hasTitles = pet.hasTitles ?: false,
                visitsSaloons = pet.visitsSaloons ?: false,
                petEducation = pet.petEducation?.map {
                    Education(
                        educationId = it.educationId,
                        name = it.name
                    )
                } ?: listOf()
            )
        )
    }
    return result
}