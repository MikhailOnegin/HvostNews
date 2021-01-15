package ru.hvost.news.data.api.response

import java.util.stream.Stream

data class OfflineSeminarsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OfflineLesson>?
) {
    data class OfflineLesson(
        val id: Int?,
        val title: String?,
        val description: String?,
        val imageUrl: String?,
        val isFinished: Boolean?,
        val date: String?,
        val city: String?,
        val participate:Boolean?,
        val sponsor: String?,
        val subscriptionEvent: Boolean?,
        val partners: List<Partner>?,
        val videos: List<Video>?,
        val wait: List<Wait>?,
        val petSchedules: List<PetSchedule>?
    )
    data class Video(
        val title: String?,
        val imageVideoUrl:String?,
        val videoUrl: String?
    )
    data class Partner(
        val name: String?,
        val image: String?
    )
    data class PetSchedule(
        val petTypeId: String?,
        val petTypeName: String?,
        val schedules: List<Schedule>?
    )
    data class Schedule(
        val title: String?,
        val description: String?,
        val date: String?,
        val timeStart: String?,
        val timeFinish: String?
    )
    data class Wait(
        val imageUrl: String?,
        val head: String?
    )
}