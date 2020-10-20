package ru.hvost.news.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.CouponInfoResponse
import ru.hvost.news.data.api.response.CouponsResponse
import ru.hvost.news.models.CouponInfo
import ru.hvost.news.models.Coupons
import ru.hvost.news.models.toDomain
import ru.hvost.news.utils.enums.State

class CouponViewModel:ViewModel() {

    private val mutableCouponsState:MutableLiveData<State> = MutableLiveData()
    val couponsState:LiveData<State> = mutableCouponsState

    private val  mutableCoupons:MutableLiveData<Coupons> = MutableLiveData()
    val coupons:LiveData<Coupons> = mutableCoupons

    fun getCoupons(userToken:String){
        viewModelScope.launch {
            mutableCouponsState.value = State.LOADING
            try {
                val response = APIService.API.getCouponsAsync(userToken).await()
                mutableCoupons.value = response.toDomain()
                if (response.result == "success") mutableCouponsState.value = State.SUCCESS
                else mutableCouponsState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("loading ERROR", "GET COUPONS ${exc.message}")
                mutableCouponsState.value = State.FAILURE
            }
        }
    }

    private val mutableCouponsInfoState:MutableLiveData<State> = MutableLiveData()
    val couponsInfoState:LiveData<State> = mutableCouponsInfoState

    private val mutableCouponsInfo:MutableLiveData<CouponInfo> = MutableLiveData()
    val couponsInfo:LiveData<CouponInfo> = mutableCouponsInfo

    fun getCouponsInfo(userToken: String){
        viewModelScope.launch {
            mutableCouponsInfoState.value = State.LOADING
            try {
                val response = APIService.API.getCouponsInfoAsync(userToken).await()
                mutableCouponsInfo.value = response.toDomain()
                if (response.result == "success") mutableCouponsInfoState.value = State.SUCCESS
                else mutableCouponsInfoState.value = State.ERROR
            } catch (exc: Exception) {
                mutableCouponsInfoState.value = State.FAILURE
            }
        }
    }
}