package ru.hvost.news.models

import ru.hvost.news.data.api.response.Food

data class PetFood(
    val id: String,
    val foodName: String
)

fun List<Food>.toFood(): List<PetFood> {
    val result = mutableListOf<PetFood>()
    for (food in this) {
        result.add(
            PetFood(
                id = food.id ?: "",
                foodName = food.foodName ?: "",
            )
        )
    }
    return result
}