package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.data.api.response.ShopsResponse
import ru.hvost.news.utils.getUriForBackendImagePath
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
    val promotions: List<Promotion>,
    var isFavourite: Boolean = false
) {

    data class Promotion(
        val title: String,
        val imageUrl: Uri,
        val description: String
    )

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
            typeShopName = shop.typeShopName.orEmpty(),
            promotions = shop.promotions?.map { Shop.Promotion(
                title = it.title.orEmpty(),
                imageUrl = getUriForBackendImagePath(it.imageUrl),
                description = it.description.orEmpty()
            ) } ?: listOf()
        )
    }
}