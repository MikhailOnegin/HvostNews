package ru.hvost.news.data.api.response

import java.io.Serializable


data class CouponsResponse (
    val result:String?,
    val error:String?,
    val coupons:List<CouponResponse>?
){
    data class CouponResponse (
        val couponId:String?,
        val imageUrl:String?,
        val isUsed:String?,
        val title:String?,
        val shortDescription:String?,
        val description:String?,
        val expirationDate:String?,
        val qrCode:String?
    ): Serializable
}