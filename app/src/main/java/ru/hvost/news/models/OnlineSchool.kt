package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import java.io.Serializable

data class OnlineSchool(
    val schools: List<School>
) {
    data class School(
        val domainId: Int,
        val id: Long,
        val title: String,
        val image: String,
        val userRank: String,
        val images: String,
        val description: String
    ): Serializable
}
fun OnlineSchoolsResponse.toDomain(): OnlineSchool{
    return OnlineSchool(
        schools = this.schools.toDomain()
    )
    }

fun  List<OnlineSchoolsResponse.SchoolResponse>?.toDomain(): List<OnlineSchool.School> {
    val result = mutableListOf<OnlineSchool.School>()
    this?.run {
        for ((index, schoolResponse) in this.withIndex()) {
            result.add(
                OnlineSchool.School(
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