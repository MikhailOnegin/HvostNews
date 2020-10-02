package ru.hvost.news.data.api.response

data class CouponsResponse (
    val result:String?,
    val error:String?,
    val coupons:List<Coupon>?
){
    data class Coupon (
       val couponId:Long?,
       val imageUrl:String?,
       val isUsed:Boolean?,
       val title:String?,
       val shortDescription:String?,
       val description:String?,
       val experationDate:String?,
       val qrCode:String?
    )
}