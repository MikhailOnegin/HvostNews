package ru.hvost.news.presentation.fragments.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.VisibleRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.Shop
import ru.hvost.news.models.toShops
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class MapViewModel: ViewModel() {

    private val _shopsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val shopsLoadingEvent: LiveData<NetworkEvent<State>> = _shopsLoadingEvent
    private val _shops = MutableLiveData<List<Shop>>()
    val shops: LiveData<List<Shop>> = _shops
    private var originShopsList = listOf<Shop>()

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

    var promotions = listOf<Shop.Promotion>()
    val selectedPromotion = MutableLiveData<Shop.Promotion>()

    fun setShopIsFavourite(shopId: String, isFavourite: Boolean) {
        viewModelScope.launch {
            try {
                APIService.API.setIsShopFavouriteAsync(
                    userToken = App.getInstance().userToken,
                    shopId = shopId,
                    isFavourite = isFavourite.toString()
                ).await()
            } catch (exc: Exception) {}
        }
    }

    private var collapseDistance = 0.0
    private val _suggestionsList = MutableLiveData<List<Shop>>()
    val suggestionsList: LiveData<List<Shop>> = _suggestionsList

    private var updateJob: Job? = null

    fun processShopsDrawing(
        visibleRegion: VisibleRegion,
        cameraPosition: CameraPosition,
        zoom: Double
    ){
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            calculateCollapseDistance(cameraPosition)
            val filteredList = getFilteredShopsList()
            val truncatedList = truncateShops(filteredList, visibleRegion)
            _suggestionsList.postValue(filteredList.toList())
            sendDrawEvents(truncatedList, zoom)
        }
    }

    private fun getFilteredShopsList(): List<Shop> {
        var result = originShopsList
        if (!showGrooms) result = result.filter { it.typeShopId != GROOMS_ID }
        if (!showVets) result = result.filter { it.typeShopId != VETS_ID }
        if (!showZoos) result = result.filter { it.typeShopId != ZOOS_ID }
        if (showPromos) result = result.filter { it.promotions.isNotEmpty() }
        return result
    }

    private fun truncateShops(shops: List<Shop>, visibleRegion: VisibleRegion): List<Shop> {
        return shops.filter {
            isShopInVisibleArea(it, visibleRegion)
        }
    }

    private val _shopsOnMap = MutableLiveData<Map<String, Shop>>()
    val shopsOnMap: LiveData<Map<String, Shop>> = _shopsOnMap

    private fun sendDrawEvents(
        shops: List<Shop>?,
        zoom: Double
    ) {
        shops?.let {
            val totalShopsToDraw = mutableMapOf<String, Shop>()
            for (shop in shops) {
                if (hasConflicts(totalShopsToDraw.values.toList(), shop, zoom)) continue
                else totalShopsToDraw["${shop.latitude}${shop.longitude}"] = shop
            }
            _shopsOnMap.postValue(totalShopsToDraw)
        }
    }

    private fun isShopInVisibleArea(shop: Shop, region: VisibleRegion): Boolean {
        val left = min(region.topLeft.longitude, region.bottomRight.longitude)
        val width = abs(region.topLeft.longitude - region.bottomRight.longitude)
        val centerX = left + width/2
        val bottom = min(region.topLeft.latitude, region.bottomRight.latitude)
        val height = abs(region.topLeft.latitude - region.bottomRight.latitude)
        val centerY = bottom + height/2
        val drawingRadius = distanceBetweenTwoPoints(region.topLeft, region.bottomRight)
        return distanceBetweenShopAndPoint(shop, Point(centerY, centerX)) < drawingRadius
    }

    private fun hasConflicts(shopsToDraw: List<Shop>, shop: Shop, zoom: Double): Boolean {
        if (zoom >= 14.0) return false
        for (shopToDraw in shopsToDraw) {
            if (distanceBetweenTwoShops(shopToDraw, shop) < collapseDistance) return true
        }
        return false
    }

    private fun distanceBetweenTwoPoints(firstPoint: Point, secondPoint: Point): Double {
        return sqrt(
            (firstPoint.latitude - secondPoint.latitude).pow(2.0) +
                    (firstPoint.longitude - secondPoint.longitude).pow(2.0)
        )
    }

    private fun distanceBetweenShopAndPoint(shop: Shop, point: Point): Double {
        return sqrt(
            (shop.latitude - point.latitude).pow(2.0) +
                    (shop.longitude - point.longitude).pow(2.0)
        )
    }

    private fun distanceBetweenTwoShops(firstShop: Shop, secondShop: Shop): Double {
        return sqrt(
            (firstShop.latitude - secondShop.latitude).pow(2.0) +
                    (firstShop.longitude - secondShop.longitude).pow(2.0)
        )
    }

    private fun calculateCollapseDistance(cameraPosition: CameraPosition) {
        collapseDistance = if (cameraPosition.zoom < 4) 6.0
        else 8192/(cameraPosition.zoom.toDouble().pow(5.5))
    }

    companion object {

        const val GROOMS_ID = "3"
        const val VETS_ID = "1"
        const val ZOOS_ID = "2"

    }

}