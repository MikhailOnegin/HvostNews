package ru.hvost.news.models

import ru.hvost.news.data.api.response.ShopsResponse

data class Shops(
    val shops: List<Shop>
) {
    data class Shop(
        val domainId:Int,
        val latitude: Float,
        val longitude: Float,
        val name: String,
        val shortDescription: String,
        val address: String,
        val regime: String,
        val phone: String,
        val website: String
    )
}

fun ShopsResponse.toOfflineLessons(): Shops{
    return Shops(
        shops = this.shops.toOfflineLessons()
    )
    }

fun List<ShopsResponse.ShopResponse>?.toOfflineLessons(): List<Shops.Shop> {
    val result = mutableListOf<Shops.Shop>()
    this?.run {
        for ((index, shopResponse) in this.withIndex()) {
            result.add(
                Shops.Shop(
                    domainId = index,
                    latitude = shopResponse.latitude ?: 0f,
                    longitude = shopResponse.longitude ?: 0f,
                    name = shopResponse.name ?: "",
                    shortDescription = shopResponse.shortDescription ?: "",
                    address = shopResponse.address ?: "",
                    regime = shopResponse.regime ?: "",
                    phone = shopResponse.phone ?: "",
                    website = shopResponse.website ?: ""
                )
            )
        }
    }
    return result
}