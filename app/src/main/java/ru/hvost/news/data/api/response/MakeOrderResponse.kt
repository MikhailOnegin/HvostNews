package ru.hvost.news.data.api.response

data class MakeOrderResponse(
    val result: String?,
    val error: String?,
    val orderNumber: Int?
)