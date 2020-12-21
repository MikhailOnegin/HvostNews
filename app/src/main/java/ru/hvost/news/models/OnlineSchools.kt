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
        val imageDetailUrl: String,
        val userRank: String,
        val description: String,
        val participate: Boolean,
        val literatures: List<Literature>,
        val lessonsPassed: List<LessonPassed>,
        val wait :List<Wait>
    )

    data class LessonPassed(
        val number: Int,
        val isPassed: Boolean
    )

    data class Literature(
        val title: String,
        val pet: String,
        val fileUrl: String,
    )
    data class Wait(
        val head: String,
        val imageUrl: String,
    )
}
fun OnlineSchoolsResponse.toOnlineSchools(): OnlineSchools{
    return OnlineSchools(
        onlineSchools = this.schools.toOnlineSchools()
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
                    imageDetailUrl = schoolResponse.imageDetailUrl ?: "",
                    userRank = schoolResponse.userRank ?: "",
                    description = schoolResponse.description ?: "",
                    participate = schoolResponse.participate ?: false,
                    literatures = schoolResponse.literature.toLiteratures(),
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
        for (literature in this.iterator()) {
            result.add(
                OnlineSchools.Literature(
                    title = literature.title ?: "",
                    pet = literature.pet ?: "",
                    fileUrl = literature.fileUrl ?: ""
                )
            )
        }
    }
    return result
}

fun List<OnlineSchoolsResponse.Wait>?.toWait(): List <OnlineSchools.Wait>{
    val result = mutableListOf<OnlineSchools.Wait>()
    this?.run {
        for (wait in this.iterator()) {
            result.add(
                OnlineSchools.Wait(
                    head = wait.head ?: "",
                    imageUrl = wait.imageUrl ?: "",
                )
            )
        }
    }
    return result
}

fun List<OnlineSchoolsResponse.LessonPassed>?.toNotNull():List<OnlineSchools.LessonPassed>{
    val result = mutableListOf<OnlineSchools.LessonPassed>()
    this?.run {
        for (lessonPassed in this.iterator()) {
            result.add(
                OnlineSchools.LessonPassed(
                    lessonPassed.lessonNumber ?:0,
                    lessonPassed.isPassed?: false
                )
            )
        }
    }
    return result
}