package ru.hvost.news.data.api.response

data class OnlineLessonsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OnlineLesson>?
) {
    data class OnlineLesson(
        val lessonId: String?,
        val lessonTitle: String?,
        val lessonNumber: String?,
        val isTestPassed: Boolean?,
        val petAge: List<String>?,
        val videoUrl: String?,
        val imageVideoUrl: String?,
        val testQuestion: String?,
        val literatures: List<Literature>?,
        val answerList: List<Answer>?
    )

    data class Answer(
        val answer: String?,
        val isTrue: Boolean?
    )

    data class Literature(
            val title: String?,
            val pet: String?,
            val fileUrl: String?,
    )
}