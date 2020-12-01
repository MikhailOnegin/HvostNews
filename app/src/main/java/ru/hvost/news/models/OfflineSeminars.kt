package ru.hvost.news.models

import ru.hvost.news.data.api.response.OfflineSeminarsResponse

data class OfflineSeminars(
    val seminars: List<OfflineLesson>)
{
    data class OfflineLesson(
        val domainId:Int,
        val id:String,
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

fun OfflineSeminarsResponse.toOfflineLessons(): OfflineSeminars{
        return OfflineSeminars(
            seminars = this.lessons.toOfflineLessons()
        )
    }

fun List<OfflineSeminarsResponse.OfflineLesson>?.toOfflineLessons(): List<OfflineSeminars.OfflineLesson> {
    val result = mutableListOf<OfflineSeminars.OfflineLesson>()
    this?.run {
        for ((index, offlineLessonResponse) in this.withIndex()) {
            result.add(
                OfflineSeminars.OfflineLesson(
                    domainId = index,
                    id = offlineLessonResponse.id ?: "",
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