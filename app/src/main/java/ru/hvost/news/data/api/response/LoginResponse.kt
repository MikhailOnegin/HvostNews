package ru.hvost.news.data.api.response

data class LoginResponse(
    val result: String?,
    val error: String?,
    val userToken: String?
)