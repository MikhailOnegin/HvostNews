package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.data.api.response.ProductsResponse
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.getUriForBackendImagePath

sealed class ShopItem(
    val id: Long,
    val categoryId: Long = 0
)

class ShopHeader(
    id: Long,
    categoryId: Long,
    val imageUri: Uri,
    val text: String
) : ShopItem(id, categoryId)

class ShopCategory(
    id: Long,
    val name: String,
    var selectedProducts: Int,
    var isExpanded: Boolean = true
) : ShopItem(id)

class ShopMessage(
    id: Long,
    categoryId: Long,
    val message: String
) : ShopItem(id, categoryId)

class ShopProduct(
    id: Long,
    categoryId: Long,
    val imageUri: Uri,
    val description: String,
    val price: Float,
    val oldPrice: Float,
    val article: String,
    val brand: String,
    val manufacturer: String,
    val `class`: String,
    val productId: String,
    val title: String,
    val weight: String,
    val barcode: String,
    val regime: String,
    val ingredients: String,
    val contraindications: String,
    val composition: String,
    var isInCart: Boolean = false
) : ShopItem(id, categoryId)

fun List<ProductsResponse.Product>?.toShopProducts(
    categoryId: Long
): List<ShopProduct> {
    if (this == null) return listOf()
    return map {
        ShopProduct(
            id = UniqueIdGenerator.nextId(),
            categoryId = categoryId,
            imageUri = getUriForBackendImagePath(it.imageUrl),
            description = it.description.orEmpty(),
            price = it.price ?: 0f,
            oldPrice = it.oldPrice ?: 0f,
            article = it.article.orEmpty(),
            brand = it.brand.orEmpty(),
            manufacturer = it.manufacturer.orEmpty(),
            `class` = it.`class`.orEmpty(),
            productId = it.productId.orEmpty(),
            title = it.title.orEmpty(),
            weight = it.weight.orEmpty(),
            barcode = it.barcode.orEmpty(),
            regime = it.specialTemperatureRegime.orEmpty(),
            ingredients = it.ingredients.orEmpty(),
            contraindications = it.contraindications.orEmpty(),
            composition = it.composition.orEmpty(),
            isInCart = false
        )
    }
}