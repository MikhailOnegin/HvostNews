package ru.hvost.news.models

import ru.hvost.news.data.api.response.InterestsResponse

enum class CheckboxStates { SELECTED, UNSELECTED, INDETERMINATE }

data class InterestsCategory(
    val categoryId: Long,
    val categoryName: String,
    val imageUrl: String,
    var state: Enum<CheckboxStates>,
    var isExpanded: Boolean,
    var sendParent: Boolean,
) : CategoryItem(id = categoryId)

data class Interests(
    val interestId: Long,
    val interestName: String,
    val parentCategoryId: Long,
    var state: Enum<CheckboxStates>
) : CategoryItem(interestId, parentCategoryId)

sealed class CategoryItem(
    val id: Long,
    val parentId: Long = 0
)

fun List<InterestsResponse.Interest>?.toSortedInterests(): List<CategoryItem> {
    val result = mutableListOf<CategoryItem>()
    result.addAll(this?.map {
        if (it.id == it.categoryId) {
            InterestsCategory(
                categoryId = it.id?.toLong() ?: 0,
                categoryName = it.name ?: "",
                imageUrl = it.imageUrl ?: "",
                state = CheckboxStates.UNSELECTED,
                sendParent = false,
                isExpanded = false
            )
        } else {
            Interests(
                interestId = it.id?.toLong() ?: 0,
                interestName = it.name ?: "",
                parentCategoryId = it.categoryId?.toLong() ?: 0,
                state = CheckboxStates.UNSELECTED
            )
        }
    }
        ?: listOf())
    return result
}