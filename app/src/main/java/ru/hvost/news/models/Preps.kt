package ru.hvost.news.models

import ru.hvost.news.data.api.response.Preparations

data class Preps(
    val index: Long,
    val id: String,
    val name: String,
    val typeId: String
)

fun List<Preparations>.toPreps(): List<Preps> {
    val result = mutableListOf<Preps>()
    for ((index, preparation) in this.withIndex()) {
        result.add(
            Preps(
                index = index.inc().toLong(),
                id = preparation.id ?: "",
                name = preparation.name ?: "",
                typeId = preparation.typeId ?: ""
            )
        )
    }
    result.add(
        0, Preps(
            index = 0L,
            id = "0",
            name = "Не выбрано",
            typeId = "0"
        )
    )
    return result
}