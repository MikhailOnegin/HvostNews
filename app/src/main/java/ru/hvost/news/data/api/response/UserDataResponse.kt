package ru.hvost.news.data.api.response

data class UserDataResponse(
    val result: String?,
    val error: String?,
    val name: String?,
    val surname: String?,
    val patronymic: String?,
    val phone: String?,
    val email: String?,
    val city: String?,
    val birthday: String?,
    val interests: List<String>?,
    val deliveryAddresses: List<DeliveryAddress>?,
    val mailNotifications: Boolean?,
    val sendPushes: Boolean?
) {

    data class DeliveryAddress(
        val city: String?,
        val street: String?,
        val house: String?,
        val flat: String?
    )

}