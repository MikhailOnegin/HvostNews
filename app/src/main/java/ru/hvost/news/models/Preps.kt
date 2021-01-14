package ru.hvost.news.models

import ru.hvost.news.data.api.response.Preparations

data class Preps(
    val id: String,
    val name: String,
    val typeId: String
)

fun List<Preparations>.toPreps(): List<Preps> {
    val result = mutableListOf<Preps>()
    for (preparation in this) {
        result.add(
            Preps(
                id = preparation.id ?: "",
                name = preparation.name ?: "",
                typeId = preparation.typeId ?: ""
            )
        )
    }
    return result
}