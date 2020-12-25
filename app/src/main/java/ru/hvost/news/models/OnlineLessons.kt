package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineLessonsResponse

data class OnlineLessons(
    val lessons: List<OnlineLesson>
) {
    data class OnlineLesson(
        val domainId: Int,
        val lessonId:String,
        val lessonTitle: String,
        val petAge: String,
        val lessonNumber: String,
        val isTestPassed: Boolean,
        val videoUrl: String,
        val imageVideoUrl: String,
        val testQuestion: String,
        val answerList: List<Answer>
    )

    data class Answer(
        val answer: String,
        val isTrue: Boolean
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
                    petAge = onlineLessonResponse.petAge ?: "",
                    lessonNumber = onlineLessonResponse.lessonNumber ?: "",
                    isTestPassed = onlineLessonResponse.isTestPassed ?: false,
                    videoUrl = onlineLessonResponse.videoUrl ?: "",
                    imageVideoUrl = onlineLessonResponse.imageVideoUrl ?: "",
                    testQuestion = onlineLessonResponse.testQuestion ?: "",
                    answerList = onlineLessonResponse.answerList.toAnswers()
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