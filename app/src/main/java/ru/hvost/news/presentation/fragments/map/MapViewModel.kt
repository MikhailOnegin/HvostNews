package ru.hvost.news.presentation.fragments.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.Shop
import ru.hvost.news.models.toShops
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent

class MapViewModel: ViewModel() {

    private val _shopsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val shopsLoadingEvent: LiveData<NetworkEvent<State>> = _shopsLoadingEvent
    private val _shops = MutableLiveData<List<Shop>>()
    val shops: LiveData<List<Shop>> = _shops
    var originShopsList = listOf<Shop>()

    fun loadShops(userToken: String?) {
        viewModelScope.launch {
            _shopsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getShopsAsync(userToken).await()
                if (response.result == "success") {
                    _shopsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                    originShopsList = response.shops.toShops()
                    _shops.value = originShopsList
                } else {
                    _shopsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _shops.value = listOf()
                }
            } catch (exc: Exception) {
                _shopsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _shops.value = listOf()
            }
        }
    }

    private val _optionsClickedEvent = MutableLiveData<OneTimeEvent>()
    val optionsClickedEvent: LiveData<OneTimeEvent> = _optionsClickedEvent

    fun sendOptionsClickedEvent() {
        _optionsClickedEvent.value = OneTimeEvent()
    }

    var showGrooms = true
    var showVets = true
    var showZoos = true
    var showPromos = false

    val showGroomsTemp = MutableLiveData(showGrooms)
    val showVetsTemp = MutableLiveData(showVets)
    val showZoosTemp = MutableLiveData(showZoos)
    val showPromosTemp = MutableLiveData(showPromos)

    companion object {

        const val GROOMS_ID = "3"
        const val VETS_ID = "1"
        const val ZOOS_ID = "2"

    }

}