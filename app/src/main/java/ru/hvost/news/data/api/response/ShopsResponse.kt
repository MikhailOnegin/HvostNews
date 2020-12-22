package ru.hvost.news.data.api.response

data class ShopsResponse(
    val result: String?,
    val error: String?,
    val shops: List<ShopResponse>?
) {

    data class ShopResponse(
        val latitude: String?,
        val longitude: String?,
        val name: String?,
        val shortDescription: String?,
        val address: String?,
        val regime: String?,
        val phone: List<String>?,
        val website: String?
    )

}