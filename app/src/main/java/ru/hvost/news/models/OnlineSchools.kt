package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineSchoolsResponse

data class OnlineSchools(
    val onlineSchools: List<OnlineSchool>,

) {
    data class OnlineSchool(
        val domainId: Int,
        val id: Long,
        val title: String,
        val image: String,
        val userRank: String,
        val images: String,
        val description: String,
        val literatures:List<Literature>,
        val lessonsPassed:List<Boolean>,
        val wait:List<Wait>

    )

    data class Literature(
        val name:String,
        val pet:String,
        val src:String,
    )
    data class Wait(
        val head:String,
        val imageUrl:String,
        val description:String
    )
}
fun OnlineSchoolsResponse.toOnlineSchools(): OnlineSchools{
    return OnlineSchools(
        onlineSchools = this.onlineSchools.toOnlineSchools()
    )
    }

fun  List<OnlineSchoolsResponse.OnlineSchool>?.toOnlineSchools(): List<OnlineSchools.OnlineSchool> {
    val result = mutableListOf<OnlineSchools.OnlineSchool>()
    this?.run {
        for ((index, schoolResponse) in this.withIndex()) {
            result.add(
                OnlineSchools.OnlineSchool(
                    domainId = index,
                    id = schoolResponse.id ?: 0,
                    title = schoolResponse.title ?: "",
                    image = schoolResponse.image ?: "",
                    userRank = schoolResponse.userRank ?: "",
                    images = schoolResponse.images ?: "",
                    description = schoolResponse.description ?: "",
                    literatures = schoolResponse.literatures.toLiteratures(),
                    lessonsPassed = schoolResponse.lessonsPassed.toNotNull(),
                    wait = schoolResponse.wait.toWait()

                )
            )
        }
    }
    return result
}

fun List<OnlineSchoolsResponse.Literature>?.toLiteratures():List<OnlineSchools.Literature>{
    val result = mutableListOf<OnlineSchools.Literature>()
    this?.run {
        for ((index, literature) in this.withIndex()) {
            result.add(
                OnlineSchools.Literature(
                    name = literature.name ?: "",
                    pet = literature.pet ?: "",
                    src = literature.src ?: ""
                )
            )
        }
    }
    return result
}

fun List<OnlineSchoolsResponse.Wait>?.toWait(): List <OnlineSchools.Wait>{
    val result = mutableListOf<OnlineSchools.Wait>()
    this?.run {
        for ((index, wait) in this.withIndex()) {
            result.add(
                OnlineSchools.Wait(
                    head = wait.head ?: "",
                    imageUrl = wait.imageUrl ?: "",
                    description = wait.description ?:""
                )
            )
        }
    }
    return result
}

fun List<Boolean>?.toNotNull():List<Boolean>{
    val result = mutableListOf<Boolean>()
    this?.run {
        for ((index, b) in this.withIndex()) {
            result.add(
                b ?: false
            )
        }
    }
    return result
}