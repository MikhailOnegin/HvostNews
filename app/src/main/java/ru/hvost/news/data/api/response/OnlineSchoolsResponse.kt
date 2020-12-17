package ru.hvost.news.data.api.response

data class OnlineSchoolsResponse(
    val result: String?,
    val error: String?,
    val schools: List<OnlineSchool>?,

    ) {
    data class OnlineSchool(
        val id: Long?,
        val title: String?,
        val image: String?,
        val imageDetailUrl: String?,
        val participate:Boolean?,
        val userRank: String?,
        val description: String?,
        val images: List<String>?,
        val wait: List<Wait>?,
        val literatures: List<Literature>?,
        val lessonsPassed: List<LessonPassed>?,

    )

    data class LessonPassed(
        val lessonNumber: Int?,
        val isPassed: Boolean?
    )

    data class Literature(
         val name: String?,
         val pet: String?,
         val src: String?,
    )
    data class Wait(
        val head: String?,
        val imageUrl: String?,
    )
}