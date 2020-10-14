package ru.hvost.news.models

import ru.hvost.news.data.api.response.OfflineLessonsResponse

data class OfflineLessons(
    val lessons: List<OfflineLesson>)
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

fun OfflineLessonsResponse.toDomain(): OfflineLessons{
        return OfflineLessons(
            lessons = this.lessonResponses.toDomain()
        )
    }

fun List<OfflineLessonsResponse.OfflineLessonResponse>?.toDomain(): List<OfflineLessons.OfflineLesson> {
    val result = mutableListOf<OfflineLessons.OfflineLesson>()
    this?.run {
        for ((index, offlineLessonResponse) in this.withIndex()) {
            result.add(
                OfflineLessons.OfflineLesson(
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