package ru.hvost.news.models

import kotlin.random.Random

class Prize(
    val id: Long,
    val name: String,
    val breed: String
){
    companion object{
        fun getTestPrizeList(): List<Prize>{
            val result = mutableListOf<Prize>()
            for(i in 0L until 2L){
                result.add(
                    Prize(
                        id = i + 1,
                        name = listOf("Собака", "Кошка", "Псина")[Random.nextInt(3)],
                        breed = listOf("Собаки", "Кошки", "Птицы", "Черепахи")[Random.nextInt(4)]
                    )
                )
            }
            return result
        }
    }
}