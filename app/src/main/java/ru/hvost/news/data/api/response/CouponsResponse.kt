package ru.hvost.news.data.api.response

import java.io.Serializable


data class CouponsResponse (
    val result:String?,
    val error:String?,
    val coupons:List<Coupon>?
){
    data class Coupon (
        val couponId:String?,
        val imageUrl:String?,
        val isUsed:Boolean?,
        val title:String?,
        val shortDescription:String?,
        val description:String?,
        val expirationDate:String?,
        val address:String?,
        val qRImageUrl:String?,
        val qrCode:String?
    ): Serializable
}