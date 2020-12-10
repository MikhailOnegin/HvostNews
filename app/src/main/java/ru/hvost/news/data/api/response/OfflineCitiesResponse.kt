package ru.hvost.news.data.api.response

data class  OfflineCitiesResponse (
val result: String?,
val error: String?,
val cities:List<CityOffline>?
)
{
    data class CityOffline(
        val cityId: String?,
        val name: String?
    )
}