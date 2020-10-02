package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.data.api.response.CouponsInfoResponse
import ru.hvost.news.data.api.response.CouponsResponse
import ru.hvost.news.utils.enums.State

class CouponViewModel:ViewModel() {

    private val mutableCouponsState:LiveData<State> = MutableLiveData()
    val userTokenState:LiveData<State> = mutableCouponsState
    private val  mutableCoupons:LiveData<CouponsResponse> = MutableLiveData()
    val coupons:LiveData<CouponsResponse> = mutableCoupons

    fun getCoupons(userToken:String){

    }

    private val mutableCouponsInfoState:LiveData<State> = MutableLiveData()
    val couponsInfoState:LiveData<State> = mutableCouponsInfoState
    private val mutableCouponsInfo:LiveData<CouponsInfoResponse> = MutableLiveData()
    val couponsInfo:LiveData<CouponsInfoResponse> = mutableCouponsInfo

    fun getCouponsInfo(){

    }
}