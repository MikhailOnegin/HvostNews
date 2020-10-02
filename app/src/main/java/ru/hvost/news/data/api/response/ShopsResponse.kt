package ru.hvost.news.data.api.response

data class ShopsResponse (
    val result:String?,
    val error:String?,
    val shops:List<Shop>?
)
{
    data class Shop(
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