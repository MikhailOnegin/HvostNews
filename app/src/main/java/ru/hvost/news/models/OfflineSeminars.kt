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
        val participate: Boolean,
        val videos: List<Video>,
        val partners: List<Partner>,
        val schedule: List<Schedule>
    )
    data class Video(
        val title:String?,
        val videoUrl:String?
    )
    data class Partner(
        val title:String?,
        val imageUrl:String?
    )
    data class Schedule(
        val title: String,
        val date:String,
        val timeStart: String,
        val timeFinish: String
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
                    participate = offlineLessonResponse.participate ?: false,
                    schedule = offlineLessonResponse.schedule.toSchedules(),
                    videos = offlineLessonResponse.videos.toVideos(),
                    partners = offlineLessonResponse.partners.toPartners()
                )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.Video>?.toVideos(): List<OfflineSeminars.Video>{
    val result = mutableListOf<OfflineSeminars.Video>()
    this?.run {
        for ((index, video) in this.withIndex()) {
            result.add(
                OfflineSeminars.Video(
                    title = video.title ?: "",
                    videoUrl = video.videoUrl ?: ""
                )
            )
        }
    }
    return result
}
fun List<OfflineSeminarsResponse.Partner>?.toPartners(): List<OfflineSeminars.Partner>{
    val result = mutableListOf<OfflineSeminars.Partner>()
    this?.run {
        for ((index, partner) in this.withIndex()) {
            result.add(
                OfflineSeminars.Partner(
                    title = partner.title ?: "",
                    imageUrl = partner.imageUrl ?: ""
                )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.Schedule>?.toSchedules(): List<OfflineSeminars.Schedule>{
    val result = mutableListOf<OfflineSeminars.Schedule>()
    this?.run {
        for ((index, schedule) in this.withIndex()) {
            result.add(
                OfflineSeminars.Schedule(
                    title = schedule.title ?: "",
                    date =  schedule.date ?: "",
                    timeStart =  schedule.timeStart?: "",
                    timeFinish = schedule.timeFinish?: ""
                )
            )
        }
    }
    return result
}