package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.CouponInfo
import ru.hvost.news.models.Coupons
import ru.hvost.news.models.toCouponsInfo
import ru.hvost.news.models.toOfflineLessons
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class CouponViewModel : ViewModel() {

    private val _couponsEvent: MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val couponsEvent: LiveData<NetworkEvent<State>> = _couponsEvent

    private val _coupons: MutableLiveData<Coupons> = MutableLiveData()
    val coupons: LiveData<Coupons> = _coupons
    var couponsCount: Int? = null

    fun getCoupons(userToken: String) {
        viewModelScope.launch {
            _couponsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getCouponsAsync(userToken).await()
                _coupons.value = response.toOfflineLessons()
                couponsCount = _coupons.value?.coupons?.size
                if (response.result == "success") {
                    _couponsEvent.value = NetworkEvent(State.SUCCESS)
                } else _couponsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _couponsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _couponsInfoEvent: MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val couponsInfoEvent: LiveData<NetworkEvent<State>> = _couponsInfoEvent

    private val mutableCouponsInfo: MutableLiveData<CouponInfo> = MutableLiveData()
    val couponsInfo: LiveData<CouponInfo> = mutableCouponsInfo

    fun getCouponsInfo() {
        viewModelScope.launch {
            _couponsInfoEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getCouponsInfoAsync().await()
                mutableCouponsInfo.value = response.toCouponsInfo()
                if (response.result == "success") _couponsInfoEvent.value = NetworkEvent(State.SUCCESS)
                else _couponsInfoEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _couponsInfoEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }
}