package ru.hvost.news.data.api.response

data class  OfflineCitiesResponse (
val result:String?,
val error:String?,
val cities:List<CityOfflineResponse>?
)
{
    data class CityOfflineResponse(
        val cityId:String?,
        val name:String?
    )
}