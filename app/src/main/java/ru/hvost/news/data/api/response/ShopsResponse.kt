package ru.hvost.news.data.api.response

data class ShopsResponse (
    val result:String?,
    val error:String?,
    val shopResponses:List<ShopResponse>?
)
{
    data class ShopResponse(
        val latitude:Float?,
        val longitude:Float?,
        val name:String?,
        val shortDescription:String?,
        val address:String?,
        val regime:String?,
        val phone:String?,
        val website:String?
    )
}