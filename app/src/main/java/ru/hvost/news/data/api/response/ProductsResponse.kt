package ru.hvost.news.data.api.response

data class ProductsResponse(
    val result: String?,
    val error: String?,
    val domains: List<Domain>?
) {

    data class Domain(
        val domainId: String?,
        val domainTitle: String?,
        val domainDescription: String?,
        val domainImageUrl: String?,
        val categories: List<Category>?
    )

    data class Category(
        val categoryId: String?,
        val categoryTitle: String?,
        val categoryDescription: String?,
        val products: List<Product>?
    )

    data class Product(
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
        val contraindications: String?,
        val composition: String?
    )

}