package ru.hvost.news.data.api.response

data class ProductsResponse(
    val result: String?,
    val error: String?,
    val products: List<Product>?
) {

    data class Product(
        val domain: String?,
        val domainId: String?,
        val category: String?,
        val categoryId: String?,
        val productId: String?,
        val title: String?,
        val imageUrl: String?,
        val article: String?,
        val price: Float?,
        val oldPrice: Float?,
        val brand: String?,
        val manufacturer: String?,
        val `class`: String?,
        val weight: String?,
        val barcode: String?,
        val specialTemperatureRegime: String?,
        val description: String?,
        val ingredients: String?,
        val contraindications: String?
    )

}