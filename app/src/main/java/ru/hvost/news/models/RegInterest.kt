package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.InterestsResponse
import ru.hvost.news.utils.emptyImageUri

data class RegInterest(
    val id: Long,
    val interestId: String,
    val interestName: String,
    val imageUri: Uri,
    var isSelected: Boolean = false
)

fun List<InterestsResponse.Interest>?.toRegistrationInterests(): List<RegInterest> {
    val result = mutableListOf<RegInterest>()
    this?.run {
        result.addAll(distinctBy { it.categoryId }.mapIndexed { index, interest ->
            val imageUri = if(!interest.imageUrl.isNullOrBlank()){
                Uri.parse(APIService.baseUrl + interest.imageUrl)
            }else emptyImageUri
            RegInterest(
                id = index.toLong().inc(),
                interestId = interest.categoryId ?: "-1",
                interestName = interest.categoryName ?: "no name",
                imageUri = imageUri
            )
        })
    }
    return result
}