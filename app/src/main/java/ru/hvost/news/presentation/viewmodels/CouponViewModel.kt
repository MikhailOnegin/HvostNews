package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.CouponInfoResponse
import ru.hvost.news.data.api.response.CouponsResponse
import ru.hvost.news.utils.enums.State

class CouponViewModel:ViewModel() {

    private val mutableCouponsState:MutableLiveData<State> = MutableLiveData()
    val couponsState:LiveData<State> = mutableCouponsState

    private val  mutableCoupons:MutableLiveData<CouponsResponse> = MutableLiveData()
    val coupons:LiveData<CouponsResponse> = mutableCoupons

    fun getCoupons(userToken:String){
        viewModelScope.launch {
            mutableCouponsState.value = State.LOADING
            try {
                val response = APIService.API.getCouponsAsync(userToken).await()
                mutableCoupons.value = response
                if (response.result == "success") mutableCouponsState.value = State.SUCCESS
                else mutableCouponsState.value = State.ERROR
            } catch (exc: Exception) {
                mutableCouponsState.value = State.FAILURE
            }
        }
    }

    private val mutableCouponsInfoState:MutableLiveData<State> = MutableLiveData()
    val couponsInfoState:LiveData<State> = mutableCouponsInfoState

    private val mutableCouponsInfo:MutableLiveData<CouponInfoResponse> = MutableLiveData()
    val couponsInfo:LiveData<CouponInfoResponse> = mutableCouponsInfo

    fun getCouponsInfo(){

    }
}