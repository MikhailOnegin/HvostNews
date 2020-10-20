package ru.hvost.news.data.api.response

data class UserDataResponse(
    val result: String?,
    val name: String?,
    val surname: String?,
    val patronymic: String?,
    val phone: String?,
    val email: String?,
    val city: String?,
    val birthday: String?,
    val interests: List<String>?
)