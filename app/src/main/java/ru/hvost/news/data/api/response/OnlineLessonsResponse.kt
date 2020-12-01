package ru.hvost.news.data.api.response

data class OnlineLessonsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OnlineLesson>?
) {
    data class OnlineLesson(
        val lessonId:String?,
        val lessonTitle: String?,
        val petAge: String?,
        val lessonNumber: Int?,
        val isFinished: Boolean?,
        val videoUrl: String?,
        val testQuestion: String?,
        val answersList: List<Answer>?
    )

    data class Answer(
        val answer: String?,
        val isTrue: Boolean?
    )
}