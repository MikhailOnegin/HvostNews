package ru.hvost.news.models

import ru.hvost.news.data.api.response.InterestsResponse

enum class CheckboxStates { SELECTED, UNSELECTED, INDETERMINATE }

data class InterestsCategory(
    val categoryId: String,
    val categoryName: String,
    var state: Enum<CheckboxStates>,
    var sendParent: Boolean,
    val interests: List<Interests>
) : CategoryItem() {
    data class Interests(
        val interestId: String,
        val interestName: String,
        var state: Enum<CheckboxStates>,
        val imageUri: String
    )
}

object FilterFooter : CategoryItem()

sealed class CategoryItem

fun List<InterestsResponse.Interest>?.toSortedInterests(): List<CategoryItem> {
    val result = mutableListOf<CategoryItem>()
    result.addAll(this?.distinctBy { it.categoryId }
        ?.map {
            InterestsCategory(
                categoryId = it.categoryId ?: "",
                categoryName = it.categoryName ?: "",
                state = CheckboxStates.UNSELECTED,
                sendParent = false,
                interests = this.filter { item ->
                    item.categoryId == it.categoryId && item.id != it.categoryId
                }.map { interest ->
                    InterestsCategory.Interests(
                        interestId = interest.id ?: "",
                        interestName = interest.name ?: "",
                        state = CheckboxStates.UNSELECTED,
                        imageUri = interest.imageUrl ?: ""
                    )
                }
            )
        }
        ?: listOf())
    return result
}