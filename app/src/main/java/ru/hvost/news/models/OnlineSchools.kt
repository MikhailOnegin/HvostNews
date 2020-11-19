package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import java.io.Serializable

data class OnlineSchools(
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
fun OnlineSchoolsResponse.toDomain(): OnlineSchools{
    return OnlineSchools(
        schools = this.schools.toDomain()
    )
    }

fun  List<OnlineSchoolsResponse.SchoolResponse>?.toDomain(): List<OnlineSchools.School> {
    val result = mutableListOf<OnlineSchools.School>()
    this?.run {
        for ((index, schoolResponse) in this.withIndex()) {
            result.add(
                OnlineSchools.School(
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