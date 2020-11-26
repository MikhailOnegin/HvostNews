package ru.hvost.news.data.api.response

data class OnlineSchoolsResponse(
    val result: String?,
    val error: String?,
    val onlineSchools: List<OnlineSchool>?,

) {
    data class OnlineSchool(
        val id: Long?,
        val title: String?,
        val image: String?,
        val userRank: String?,
        val images: String?,
        val description: String?,
        val literatures:List<Literature>?,
        val lessonsPassed:List<Boolean>?,
        val wait:List<Wait>?
    )

    data class Literature(
         val name:String?,
         val pet:String?,
         val src:String?,
    )
    data class Wait(
        val head:String?,
        val imageUrl:String?,
        val description:String?
    )
}