package ru.hvost.news.models

import kotlin.random.Random

class PrizePrice(
    val id: Long,
    val cost: String,
){
    companion object{
        fun getTestPriceList(): List<PrizePrice>{
            val result = mutableListOf<PrizePrice>()
            for(i in 0L until 2L){
                result.add(
                    PrizePrice(
                        id = i + 1,
                        cost = listOf("2", "4", "8", "16")[Random.nextInt(4)]
                    )
                )
            }
            return result
        }
    }
}