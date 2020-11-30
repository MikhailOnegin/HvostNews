package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.response.ProductsResponse
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.emptyImageUri
import ru.hvost.news.utils.getUriForBackendImagePath

sealed class ShopItem(
    val id: Long,
    val categoryId: Long = 0
) {

    companion object {

        fun getTestList() : List<ShopItem> {
            val result = mutableListOf<ShopItem>()
            var id = 0L
            for(i in 0 until 10) {
                if(i == 7) {
                    val category = ShopCategory(
                        id = ++id,
                        name = "Пустая категория",
                        selectedProducts = 0
                    )
                    result.add(category)
                    result.add(ShopMessage(
                        id = ++id,
                        categoryId = category.id,
                        message = "Вы уже заказывали товар из данной категории."
                    ))
                    continue
                }
                val category = ShopCategory(
                    id = ++id,
                    name = "Категория $i",
                    selectedProducts = 0
                )
                result.add(category)
                result.add(
                    ShopHeader(
                        id = ++id,
                        categoryId = category.id,
                        imageUri = emptyImageUri,
                        text = App.getInstance().getString(R.string.rvHeaderTestStub)
                    )
                )
                for(j in 0 until 5) {
                    result.add(
                        ShopProduct(
                            id = ++id,
                            categoryId = category.id,
                            imageUri = emptyImageUri,
                            description = "CANAGAN Grain Free, Free-Range Chicken, корм 500 гр для мелких пород собак всех возрастов...",
                            price = 12850f,
                            oldPrice = 0f,
                            isInCart = false
                        )
                    )
                }
            }
            return result
        }

    }

}

class ShopHeader(
    id: Long,
    categoryId: Long,
    val imageUri: Uri,
    val text: String
) : ShopItem(id, categoryId)

class ShopCategory(
    id: Long,
    val name: String,
    val selectedProducts: Int,
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
    var isInCart: Boolean = false
) : ShopItem(id, categoryId)

fun List<ProductsResponse.Product>.toShopProducts(
    categoryId: Long
): List<ShopProduct> {
    return map {
        ShopProduct(
            id = UniqueIdGenerator.nextId(),
            categoryId = categoryId,
            imageUri = getUriForBackendImagePath(it.imageUrl),
            description = it.description ?: "",
            price = it.price ?: 0f,
            oldPrice = it.oldPrice ?: 0f,
            isInCart = false
        )
    }
}