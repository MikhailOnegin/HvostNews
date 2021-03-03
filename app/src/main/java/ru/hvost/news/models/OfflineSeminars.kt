package ru.hvost.news.models

import ru.hvost.news.data.api.response.OfflineSeminarsResponse

data class OfflineSeminars(
    val seminars: List<OfflineSeminar>)
{
    data class OfflineSeminar(
        val domainId:Int,
        val id: Long,
        val title: String,
        val description: String,
        val imageUrl: String,
        var isFinished: Boolean,
        val date: String,
        val city: String,
        var participate:Boolean,
        val sponsor: String,
        val subscriptionEvent: Boolean,
        val seminarUrl:String,
        val partners: List<Partner>,
        val videos: List<Video>,
        val wait: List<Wait>,
        val petSchedules: List<PetSchedule>
    )
    data class Video(
        val title: String,
        val imageVideoUrl:String,
        val videoUrl: String
    )
    data class Partner(
        val name: String,
        val image: String,
        val website: String
    )
    data class PetSchedule(
        val petTypeId: String,
        val petTypeName: String,
        val schedules: List<Schedule>
    )
    data class Schedule(
        val title: String,
        val description: String,
        val date: String,
        val timeStart: String,
        val timeFinish: String
    )
    data class Wait(
        val imageUrl: String,
        val head: String
    )
}

fun OfflineSeminarsResponse.toOfflineLessons(): OfflineSeminars{
        return OfflineSeminars(
            seminars = this.lessons.toOfflineLessons()
        )
    }

fun List<OfflineSeminarsResponse.OfflineLesson>?.toOfflineLessons(): List<OfflineSeminars.OfflineSeminar> {
    val result = mutableListOf<OfflineSeminars.OfflineSeminar>()
    this?.run {
        for ((index, offlineLessonResponse) in this.withIndex()) {
            result.add(
                    OfflineSeminars.OfflineSeminar(
                            domainId = index,
                            id = offlineLessonResponse.id ?: 0,
                            title = offlineLessonResponse.title ?: "",
                            imageUrl = offlineLessonResponse.imageUrl ?: "",
                            isFinished = offlineLessonResponse.isFinished ?: false,
                            date = offlineLessonResponse.date ?: "",
                            city = offlineLessonResponse.city ?: "",
                            sponsor = offlineLessonResponse.sponsor ?: "",
                            description = offlineLessonResponse.description ?: "",
                            participate = offlineLessonResponse.participate ?: false,
                            subscriptionEvent = offlineLessonResponse.subscriptionEvent ?: false,
                            seminarUrl = offlineLessonResponse.seminarUrl ?: "",
                            videos = offlineLessonResponse.videos.toVideos(),
                            partners = offlineLessonResponse.partners.toPartners(),
                            wait = offlineLessonResponse.wait.toWait(),
                            petSchedules = offlineLessonResponse.petSchedules.toPetSchedules(),
                    )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.Video>?.toVideos(): List<OfflineSeminars.Video>{
    val result = mutableListOf<OfflineSeminars.Video>()
    this?.run {
        for (video in this.iterator()) {
            result.add(
                OfflineSeminars.Video(
                    title = video.title ?: "",
                    imageVideoUrl = video.imageVideoUrl ?: "",
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
        for ( partner in this.iterator()) {
            result.add(
                OfflineSeminars.Partner(
                        name = partner.name ?: "",
                        image = partner.image ?: "",
                        website = partner.website?: ""
                )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.Schedule>?.toSchedules(): List<OfflineSeminars.Schedule>{
    val result = mutableListOf<OfflineSeminars.Schedule>()
    this?.run {
        for (schedule in this.iterator()) {
            result.add(
                OfflineSeminars.Schedule(
                    title = schedule.title ?: "",
                    description = schedule.description ?: "",
                    date =  schedule.date ?: "",
                    timeStart =  schedule.timeStart?: "",
                    timeFinish = schedule.timeFinish?: ""
                )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.PetSchedule>?.toPetSchedules(): List<OfflineSeminars.PetSchedule>{
    val result = mutableListOf<OfflineSeminars.PetSchedule>()
    this?.run {
        for (petSchedule in this.iterator()) {
            result.add(
                OfflineSeminars.PetSchedule(
                    petTypeId = petSchedule.petTypeId ?: "",
                    petTypeName = petSchedule.petTypeName ?: "",
                    schedules = petSchedule.schedules.toSchedules()
                )
            )
        }
    }
    return result
}

fun List<OfflineSeminarsResponse.Wait>?.toWait(): List<OfflineSeminars.Wait>{
    val result = mutableListOf<OfflineSeminars.Wait>()
    this?.run {
        for (wait in this.iterator()) {
            result.add(
                OfflineSeminars.Wait(
                    imageUrl = wait.imageUrl ?: "",
                    head = wait.head ?: ""
                )
            )
        }
    }
    return result
}