package ru.hvost.news.models

import kotlin.random.Random

class Pet(
    val id: Long,
    val name: String,
    val breed: String,
    val age: Int
){
    companion object{
        fun getTestPetList(): List<Pet>{
            val result = mutableListOf<Pet>()
            for(i in 0L until 2L){
                result.add(
                    Pet(
                        id = i + 1,
                        name = listOf("Собака", "Кошка", "Псина")[Random.nextInt(3)],
                        age = listOf(2, 4, 8)[Random.nextInt(3)],
                        breed = listOf("Собаки", "Кошки", "Птицы", "Черепахи")[Random.nextInt(4)]
                    )
                )
            }
            return result
        }
    }
}