package ru.hvost.news.data.api.response

data class OfflineSeminarsResponse(
    val result: String?,
    val error: String?,
    val lessons: List<OfflineLesson>?
) {
    data class OfflineLesson(
        val title: String?,
        val description: String?,
        val imageUrl: String?,
        val isFinished: Boolean?,
        val date: String?,
        val city: String?,
        val sponsor: String?,
        val schedule: List<String>?
    )
}