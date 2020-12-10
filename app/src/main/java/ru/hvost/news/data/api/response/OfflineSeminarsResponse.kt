package ru.hvost.news.data.api.response

import java.util.stream.Stream

data class OfflineSeminarsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OfflineLesson>?
) {
    data class OfflineLesson(
        val id: String?,
        val title: String?,
        val description: String?,
        val imageUrl: String?,
        val isFinished: Boolean?,
        val date: String?,
        val city: String?,
        val participate:Boolean?,
        val sponsor: String?,
        val videos: List<Video>?,
        val partners: List<Partner>?,
        val schedule: List<Schedule>?
    )
    data class Video(
        val title: String?,
        val videoUrl: String?
    )
    data class Partner(
        val title: String?,
        val imageUrl: String?
    )
    data class Schedule(
        val title: String?,
        val date: String?,
        val timeStart: String?,
        val timeFinish: String?
    )
}