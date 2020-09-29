package ru.hvost.news.data.api.response

data class OfflineLessonsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OfflineLesson>
) {
    data class OfflineLesson(
        val title: String?,
        val imageUrl: String?,
        val isFinished: Boolean?,
        val date: String?,
        val city: String,
        val sponsor: String?,
        val description: String?,
        val schedule: List<String>?
    )
}