package ru.hvost.news.data.api.response

data class OnlineSchoolsResponse(
    val result: String?,
    val error: String?,
    val onlineSchools: List<OnlineSchool>?,

) {
    data class OnlineSchool(
        val id: Long?,
        val isRegistered:Boolean,
        val title: String?,
        val image: String?,
        val userRank: String?,
        val description: String?,
        val literatures: List<Literature>?,
        val lessonsPassed: List<LessonPassed>?,
        val wait: List<Wait>?
    )

    data class LessonPassed(
        val number: Int?,
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
        val description: String?
    )
}