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

    private val _couponsState: MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val couponsState: LiveData<NetworkEvent<State>> = _couponsState

    private val _coupons: MutableLiveData<Coupons> = MutableLiveData()
    val coupons: LiveData<Coupons> = _coupons
    var couponsCount: Int? = null

    fun getCoupons(userToken: String) {
        viewModelScope.launch {
            _couponsState.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getCouponsAsync(userToken).await()
                _coupons.value = response.toOfflineLessons()
                couponsCount = _coupons.value?.coupons?.size
                if (response.result == "success") {
                    _couponsState.value = NetworkEvent(State.SUCCESS)
                } else _couponsState.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _couponsState.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
        //val couponsList = mutableListOf<Coupons.Coupon>()
        //for(i in 0 ..10){
        //    var b = true
        //    if(i>5){
        //        b = false
        //    }
        //    couponsList.add(
        //        Coupons.Coupon(
        //            i,
        //            i.toString(),
        //            "/upload/iblock/732/index.png",
        //            b,
        //            "Акция! Скидки 25% в магазинах партнерах на один из трех кормов для стерилизованных кошек",
        //            "Используйте купон для получения скидки 25%<br>\\r\\n в магазинах партнерах на один из трех кормов<br>\\r\\n для стерилизованных кошек.<br>",
        //            "Используйте купон для получения скидки 25%<br>\\\\r\\\\n в магазинах партнерах на один из трех кормов<br>\\\\r\\\\n для стерилизованных кошек.<br" +
        //                    "Используйте купон для получения скидки 25%<br>\\r\\n в магазинах партнерах на один из трех кормов<br>\\r\\n для стерилизованных кошек.<br" +
        //                    "Используйте купон для получения скидки 25%<br>\\r\\n в магазинах партнерах на один из трех кормов<br>\\r\\n для стерилизованных кошек.<br" +
        //                    "Используйте купон для получения скидки 25%<br>\\r\\n в магазинах партнерах на один из трех кормов<br>\\r\\n для стерилизованных кошек.<br" +
        //                    "Используйте купон для получения скидки 25%<br>\\r\\n в магазинах партнерах на один из трех кормов<br>\\r\\n для стерилизованных кошек.<br",
        //            "30.09.2020",
        //            "Ветеринарная клиника Бетховен \nг.Москва ул.Пушкина д.3 кв 23",
        //            "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
        //            "qw-ewqe-rq-we"
//
        //        ))
        //}
        //val coupons = Coupons(couponsList)
        //mutableCoupons.value = coupons
    }

    private val _couponsInfoState: MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val couponsInfoState: LiveData<NetworkEvent<State>> = _couponsInfoState

    private val mutableCouponsInfo: MutableLiveData<CouponInfo> = MutableLiveData()
    val couponsInfo: LiveData<CouponInfo> = mutableCouponsInfo

    fun getCouponsInfo() {
        viewModelScope.launch {
            _couponsInfoState.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getCouponsInfoAsync().await()
                mutableCouponsInfo.value = response.toCouponsInfo()
                if (response.result == "success") _couponsInfoState.value = NetworkEvent(State.SUCCESS)
                else _couponsInfoState.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _couponsInfoState.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }
}