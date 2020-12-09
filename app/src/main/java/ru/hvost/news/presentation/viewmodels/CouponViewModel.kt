package ru.hvost.news.presentation.viewmodels

import android.util.Log
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

    private val _mutableCouponsLoadingEvent: MutableLiveData<NetworkEvent<State>> = MutableLiveData<NetworkEvent<State>>()
    val couponsLoadingEvent: LiveData<NetworkEvent<State>> = _mutableCouponsLoadingEvent

    private val _mutableCoupons: MutableLiveData<Coupons> = MutableLiveData()
    val coupons: LiveData<Coupons> = _mutableCoupons
    var couponsCount: Int? = null

    fun getCoupons(userToken: String) {
        viewModelScope.launch {
            _mutableCouponsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getCouponsAsync(userToken).await()
                _mutableCoupons.value = response.toOfflineLessons()
                couponsCount = _mutableCoupons.value?.coupons?.size
                if (response.result == "success") {
                    _mutableCouponsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else _mutableCouponsLoadingEvent.value = NetworkEvent(State.ERROR)
            } catch (exc: Exception) {
                Log.i("eeee", "getCoupons() ERROR: ${exc.message}")
                _mutableCouponsLoadingEvent.value = NetworkEvent(State.FAILURE)
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

    private val mutableCouponsInfoState: MutableLiveData<State> = MutableLiveData()
    val couponsInfoState: LiveData<State> = mutableCouponsInfoState

    private val mutableCouponsInfo: MutableLiveData<CouponInfo> = MutableLiveData()
    val couponsInfo: LiveData<CouponInfo> = mutableCouponsInfo

    fun getCouponsInfo() {
        viewModelScope.launch {
            mutableCouponsInfoState.value = State.LOADING
            try {
                val response = APIService.API.getCouponsInfoAsync().await()
                mutableCouponsInfo.value = response.toCouponsInfo()
                if (response.result == "success") mutableCouponsInfoState.value = State.SUCCESS
                else mutableCouponsInfoState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", "getCouponsInfo() ${exc.message.toString()}")
                mutableCouponsInfoState.value = State.FAILURE
            }
        }
            //val couponInfo = CouponInfo("/upload/iblock/a74/shor_shkola273kh211_web.jpg",
        //"Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание" +
        //        "Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание Текстовое описание"
            //
            //    )
        //mutableCouponsInfo.value  = couponInfo
    }
}