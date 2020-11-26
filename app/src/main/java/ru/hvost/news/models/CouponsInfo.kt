package ru.hvost.news.models

import ru.hvost.news.data.api.response.CouponInfoResponse

data class CouponInfo(
    val imageUrl: String,
    val description: String
)

fun CouponInfoResponse.toOfflineLessons(): CouponInfo {
    return CouponInfo(
        imageUrl = this.imageUrl ?: "",
        description = this.description ?: ""
    )
}