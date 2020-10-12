package ru.hvost.news.data.api.response

data class CouponsResponse (
    val result:String?,
    val coupons:List<Coupon>?
){
    data class Coupon (
       val couponId:String?,
       val imageUrl:String?,
       val isUsed:String?,
       val title:String?,
       val shortDescription:String?,
       val description:String?,
       val experationDate:String?,
       val qrCode:String?
    )

}