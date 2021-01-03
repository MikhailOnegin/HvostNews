package ru.hvost.news.models

import ru.hvost.news.data.api.response.ShopsResponse
import ru.hvost.news.utils.tryParseDoubleValue

data class Shop(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val shortDescription: String,
    val address: String,
    val regime: String,
    val phones: List<String>,
    val website: String,
    val typeShopId: String,
    val typeShopName: String,
    var isFavourite: Boolean = false
) {

    override fun toString(): String {
        return name
    }

}

fun List<ShopsResponse.ShopResponse>?.toShops(): List<Shop> {
    if(this == null) return listOf()
    return mapIndexed { index, shop ->
        Shop(
            id = index.toLong(),
            latitude = tryParseDoubleValue(shop.latitude),
            longitude = tryParseDoubleValue(shop.longitude),
            name = shop.name.orEmpty(),
            shortDescription = shop.shortDescription.orEmpty(),
            address = shop.address.orEmpty(),
            regime = shop.regime.orEmpty(),
            website = shop.website.orEmpty(),
            phones = shop.phone.orEmpty(),
            typeShopId = shop.typeShopId.orEmpty(),
            typeShopName = shop.typeShopName.orEmpty()
        )
    }
}