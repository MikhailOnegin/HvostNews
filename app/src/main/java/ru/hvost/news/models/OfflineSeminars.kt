package ru.hvost.news.models

import ru.hvost.news.data.api.response.OfflineSeminarsResponse

data class OfflineSeminars(
    val seminars: List<OfflineLesson>)
{
    data class OfflineLesson(
        val domainId:Int,
        val title: String,
        val imageUrl: String,
        val isFinished: Boolean,
        val date: String,
        val city: String,
        val sponsor: String,
        val description: String,
        val schedule: List<String>
    )
}

fun OfflineSeminarsResponse.toDomain(): OfflineSeminars{
        return OfflineSeminars(
            seminars = this.lessons.toDomain()
        )
    }

fun List<OfflineSeminarsResponse.OfflineSeminarsResponse>?.toDomain(): List<OfflineSeminars.OfflineLesson> {
    val result = mutableListOf<OfflineSeminars.OfflineLesson>()
    this?.run {
        for ((index, offlineLessonResponse) in this.withIndex()) {
            result.add(
                OfflineSeminars.OfflineLesson(
                    domainId = index,
                    title = offlineLessonResponse.title ?: "",
                    imageUrl = offlineLessonResponse.imageUrl ?: "",
                    isFinished = offlineLessonResponse.isFinished ?: false,
                    date = offlineLessonResponse.date ?: "",
                    city = offlineLessonResponse.city ?: "",
                    sponsor = offlineLessonResponse.sponsor ?: "",
                    description = offlineLessonResponse.description ?: "",
                    schedule = offlineLessonResponse.schedule ?: mutableListOf()
                )
            )
        }
    }
    return result
}