package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineSchoolsResponse

data class OnlineSchool(
    val schoolResponses: List<School>
)

data class School(
    val domainId:Int,
    val id: Long,
    val title: String,
    val image: String,
    val userRank: String,
    val images: String,
    val description: String
)

val List<OnlineSchoolsResponse>.toDomain: List<OnlineSchool>
    get() {
        val result = mutableListOf<OnlineSchool>()
        for ((index, onlineSchoolsResponse) in this.withIndex()) {
            result.add(
                OnlineSchool(
                    schoolResponses = onlineSchoolsResponse.schoolResponses.toDomain()
                )
            )
        }
        return result
    }

fun  List<OnlineSchoolsResponse.SchoolResponse>?.toDomain(): List<School> {
    val result = mutableListOf<School>()
    this?.run {
        for ((index, schoolResponse) in this.withIndex()) {
            result.add(
                School(
                    domainId = index,
                    id = schoolResponse.id ?: 0,
                    title = schoolResponse.title ?: "",
                    image = schoolResponse.image ?: "",
                    userRank = schoolResponse.userRank ?: "",
                    images = schoolResponse.images ?: "",
                    description = schoolResponse.description ?: ""
                )
            )
        }
    }
    return result
}