package ru.hvost.news.data.api.response

data class PetFoodResponse(
    val result: String?,
    val petFood: List<Food>,
    val error: String?
)

data class Food(
    val id: String?,
    val foodName: String?
)