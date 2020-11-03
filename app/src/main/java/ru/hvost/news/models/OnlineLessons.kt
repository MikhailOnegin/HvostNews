package ru.hvost.news.models

import ru.hvost.news.data.api.response.OnlineLessonsResponse
import java.io.Serializable

data class OnlineLessons(
    val lessons: List<OnlineLesson>
) {
    data class OnlineLesson(
        val domainId: Int,
        val lessonTitle: String,
        val petAge: String,
        val lessonNumber: Int,
        val isTestPassed: String,
        val videoUrl: String,
        val testQuestion: String,
        val answersList: List<Answer>
    ): Serializable

    data class Answer(
        val answer: String,
        val isTrue: Boolean
    )
}

fun OnlineLessonsResponse.toDomain(): OnlineLessons {
    return OnlineLessons(
        lessons = this.lessons.toDomain()
    )
}


fun List<OnlineLessonsResponse.OnlineLessonResponse>?.toDomain(): List<OnlineLessons.OnlineLesson> {
    val result = mutableListOf<OnlineLessons.OnlineLesson>()
    this?.run {
        for ((index, onlineLessonResponse) in this.withIndex()) {
            result.add(
                OnlineLessons.OnlineLesson(
                    domainId = index,
                    lessonTitle = onlineLessonResponse.lessonTitle ?: "",
                    petAge = onlineLessonResponse.petAge ?: "",
                    lessonNumber = onlineLessonResponse.lessonNumber ?: 0,
                    isTestPassed = onlineLessonResponse.isTestPassed ?: "",
                    videoUrl = onlineLessonResponse.videoUrl ?: "",
                    testQuestion = onlineLessonResponse.testQuestion ?: "",
                    answersList = onlineLessonResponse.answersList.toAnswers()
                )
            )
        }
    }
    return result
}

fun List<OnlineLessonsResponse.AnswerResponse>?.toAnswers(): List<OnlineLessons.Answer> {
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