package ru.hvost.news.models

import ru.hvost.news.data.api.response.CouponsResponse
import java.io.Serializable

data class Coupons(
    val coupons: List<Coupon>
) {

    data class Coupon(
        val domainId:Int,
        val couponId: String,
        val imageUrl: String,
        val isUsed: Boolean,
        val title: String,
        val shortDescription: String,
        val description: String,
        val expirationDate: String,
        val qrCode: String
    ) : Serializable
}

fun  CouponsResponse.toDomain():Coupons{
    return Coupons(
        coupons = this.coupons.toDomain()
    )
}

fun List<CouponsResponse.CouponResponse>?.toDomain(): List<Coupons.Coupon> {

    val result = mutableListOf<Coupons.Coupon>()
    this?.run {
        for ((index, couponResponse) in this.withIndex()) {
            result.add(
                Coupons.Coupon(
                    domainId = index,
                    couponId = couponResponse.couponId ?: "",
                    imageUrl = couponResponse.imageUrl ?: "",
                    isUsed = couponResponse.isUsed ?: true,
                    title = couponResponse.title ?: "",
                    shortDescription = couponResponse.shortDescription ?: "",
                    description = couponResponse.description ?: "",
                    expirationDate = couponResponse.expirationDate ?: "",
                    qrCode = couponResponse.qrCode ?: ""
                )
            )
        }
    }
    return result
}

