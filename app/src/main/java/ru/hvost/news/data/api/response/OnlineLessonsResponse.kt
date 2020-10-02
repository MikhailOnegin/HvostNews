package ru.hvost.news.data.api.response

data class OnlineLessonsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OnlineLesson>?
) {
    data class OnlineLesson(
        val lessonTitle: String?,
        val petAge: String?,
        val lennonNumber: Int?,
        val isTestPassed: String?,
        val videoUrl: String?,
        val testQuestion: String?,
        val answersList: List<Answer>?
    )

    data class Answer(
        val answer: String?,
        val isTrue: Boolean?
    )
}