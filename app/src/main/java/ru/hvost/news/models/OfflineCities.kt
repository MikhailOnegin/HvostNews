package ru.hvost.news.models

import ru.hvost.news.data.api.response.OfflineCitiesResponse

data class CitiesOffline (
    val cities:List<CityOffline>
){
    data class CityOffline(
        val cityId:String,
        val name:String
    )
}
fun OfflineCitiesResponse.toOfflineLessons():CitiesOffline{
    return CitiesOffline(
        cities = this.cities.toOfflineLessons()
    )
}
fun List<OfflineCitiesResponse.CityOffline>?.toOfflineLessons(): List<CitiesOffline.CityOffline> {
    val result = mutableListOf<CitiesOffline.CityOffline>()
    this?.run {
        for ((_, cityOfflineResponse) in this.withIndex()) {
            result.add(
                CitiesOffline.CityOffline(
                    cityId = cityOfflineResponse.cityId?: "",
                    name = cityOfflineResponse.name?: ""
                )
            )
        }
    }
    return result
}