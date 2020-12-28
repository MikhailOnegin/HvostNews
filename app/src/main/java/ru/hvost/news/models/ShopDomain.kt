package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.data.api.response.ProductsResponse
import ru.hvost.news.utils.getUriForBackendImagePath

data class ShopDomain(
    val id: Long,
    val domainId: String,
    val domainTitle: String,
    val imageUri: Uri
)

fun List<ProductsResponse.Domain>?.toShopDomains(): List<ShopDomain> {
    if (this == null) return listOf()
    return this.mapIndexed { index, domain ->
        ShopDomain(
            id = (index + 1).toLong(),
            domainId = domain.domainId.orEmpty(),
            domainTitle = domain.domainTitle.orEmpty(),
            imageUri = getUriForBackendImagePath(domain.domainImageUrl)
        )
    }
}