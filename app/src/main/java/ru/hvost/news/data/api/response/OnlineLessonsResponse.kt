package ru.hvost.news.data.api.response

data class OnlineLessonsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OnlineLessonResponse>?
) {
    data class OnlineLessonResponse(
        val lessonId:String?,
        val lessonTitle: String?,
        val petAge: String?,
        val lessonNumber: Int?,
        val isTestPassed: String?,
        val videoUrl: String?,
        val testQuestion: String?,
        val answersList: List<AnswerResponse>?
    )

    data class AnswerResponse(
        val answer: String?,
        val isTrue: Boolean?
    )
}