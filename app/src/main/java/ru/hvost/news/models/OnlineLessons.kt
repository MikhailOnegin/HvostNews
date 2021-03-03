package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineLessonsResponse
import ru.hvost.news.data.api.response.OnlineSchoolsResponse

data class OnlineLessons(
    val lessons: List<OnlineLesson>
) {
    data class OnlineLesson(
        val domainId: Int,
        val lessonId:String,
        val lessonTitle: String,
        val petAge: List<String>,
        val lessonNumber: String,
        val isTestPassed: Boolean,
        val videoUrl: String,
        val imageVideoUrl: String,
        val testQuestion: String,
        val literatures: List<Literature>,
        val answerList: List<Answer>
    )

    data class Answer(
        val answer: String,
        val isTrue: Boolean
    )
    data class Literature(
            val title: String,
            val pet: String,
            val fileUrl: String,
    )
}

fun OnlineLessonsResponse.toOfflineLessons(): OnlineLessons {
    return OnlineLessons(
        lessons = this.lessons.toOnlineLessons()
    )
}


fun List<OnlineLessonsResponse.OnlineLesson>?.toOnlineLessons(): List<OnlineLessons.OnlineLesson> {
    val result = mutableListOf<OnlineLessons.OnlineLesson>()
    this?.run {
        for ((index, onlineLessonResponse) in this.withIndex()) {
            result.add(
                OnlineLessons.OnlineLesson(
                        domainId = index,
                        lessonId = onlineLessonResponse.lessonId?: "",
                        lessonTitle = onlineLessonResponse.lessonTitle ?: "",
                        petAge = onlineLessonResponse.petAge.toStrings(),
                        lessonNumber = onlineLessonResponse.lessonNumber ?: "",
                        isTestPassed = onlineLessonResponse.isTestPassed ?: false,
                        videoUrl = onlineLessonResponse.videoUrl ?: "",
                        imageVideoUrl = onlineLessonResponse.imageVideoUrl ?: "",
                        testQuestion = onlineLessonResponse.testQuestion ?: "",
                        answerList = onlineLessonResponse.answerList.toAnswers(),
                        literatures = onlineLessonResponse.literatures.toLiteratures()
                )
            )
        }
    }
    return result
}

fun List<OnlineLessonsResponse.Answer>?.toAnswers(): List<OnlineLessons.Answer> {
    val result = mutableListOf<OnlineLessons.Answer>()
    this?.run {
        for ((_, answerResponse) in this.withIndex()) {
            result.add(
                OnlineLessons.Answer(
                    answer = answerResponse.answer ?: "",
                    isTrue = answerResponse.isTrue ?: false
                )
            )
        }
    }
    return result
}

fun List<String>?.toStrings():List<String>{
    val result = mutableListOf<String>()
    this?.run {
        for((_,str) in this.withIndex()){
            result.add(
                    str
            )
        }

    }
    return result
}

fun List<OnlineLessonsResponse.Literature>?.toLiteratures():List<OnlineLessons.Literature>{
    val result = mutableListOf<OnlineLessons.Literature>()
    this?.run {
        for (literature in this.iterator()) {
            result.add(
                    OnlineLessons.Literature(
                            title = literature.title ?: "",
                            pet = literature.pet ?: "",
                            fileUrl = literature.fileUrl ?: ""
                    )
            )
        }
    }
    return result
}