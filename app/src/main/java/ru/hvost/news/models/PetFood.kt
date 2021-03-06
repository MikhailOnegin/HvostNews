package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.Food

data class PetFood(
    val index: Long,
    val id: String,
    val foodName: String
)

fun List<Food>.toFood(): List<PetFood> {
    val result = mutableListOf<PetFood>()
    for ((index, food) in this.withIndex()) {
        result.add(
            PetFood(
                index = index.inc().toLong(),
                id = food.id ?: "",
                foodName = food.foodName ?: "",
            )
        )
    }
    result.add(
        0, PetFood(
            index = MainViewModel.UNSELECTED_ID,
            id = "",
            foodName = MainViewModel.UNSELECTED
        )
    )
    return result
}