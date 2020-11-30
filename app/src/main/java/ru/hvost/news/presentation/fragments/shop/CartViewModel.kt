package ru.hvost.news.presentation.fragments.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.ProductsResponse
import ru.hvost.news.models.*
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent
import kotlin.Exception

class CartViewModel : ViewModel() {

    enum class CartType { Products, Prizes }

    val currentCartType = MutableLiveData<CartType>()
    val productsCart = MutableLiveData(listOf<CartItem>())
    val prizesCart = MutableLiveData(listOf<CartItem>())
    @Volatile
    var cartChangesPermitted = false
    private val _goToMakeOrderEvent = MutableLiveData<OneTimeEvent>()
    val goToMakeOrderEvent: LiveData<OneTimeEvent> = _goToMakeOrderEvent
    val readyToMakeOrder = MutableLiveData<Boolean>()
    private val _cartUpdateEvent = MutableLiveData<NetworkEvent<State>>()
    val cartUpdateEvent: LiveData<NetworkEvent<State>> = _cartUpdateEvent
    private val _makeOrderEvent = MutableLiveData<NetworkEvent<State>>()
    val makeOrderEvent: LiveData<NetworkEvent<State>> = _makeOrderEvent
    private val _finishOrderEvent = MutableLiveData<Event<Int>>()
    val finishOrderEvent: LiveData<Event<Int>> = _finishOrderEvent

    init {
        currentCartType.value = CartType.Products
        readyToMakeOrder.value = false
    }

    fun updateCartAsync(userToken: String?) {
        _cartUpdateEvent.value = NetworkEvent(State.LOADING)
        viewModelScope.launch {
            try {
                val result = APIService.API.getCartAsync(userToken).await()
                if(result.result == "success") {
                    //sergeev: Устанавливать в зависимости от типа корзины.
                    productsCart.value = result.toCartItems()
                    _cartUpdateEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    resetCarts()
                    _cartUpdateEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch(exc: Exception) {
                resetCarts()
                _cartUpdateEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
            cartChangesPermitted = true
        }
    }

    private fun resetCarts() {
        productsCart.value = listOf()
        prizesCart.value = listOf()
    }

    private val _cartChangingEvent = MutableLiveData<NetworkEvent<State>>()
    val cartChangingEvent: LiveData<NetworkEvent<State>> = _cartChangingEvent

    fun addToCart(productId: Long, count: Int){
        cartChangesPermitted = false
        val userToken = App.getInstance().userToken
        viewModelScope.launch {
            try {
                val result = APIService.API.addToCartAsync(
                    userToken = userToken,
                    productId = productId,
                    count = count
                ).await()
                if(result.result == "success") updateCartAsync(userToken)
                else {
                    _cartChangingEvent.value = NetworkEvent(State.ERROR, result.error)
                    cartChangesPermitted = true
                }
            } catch(exc: Exception) {
                _cartChangingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                cartChangesPermitted = true
            }
        }
    }

    fun removeFromCart(productId: Long, count: Int){
        cartChangesPermitted = false
        val userToken = App.getInstance().userToken
        viewModelScope.launch {
            try {
                val result = APIService.API.removeFromCartAsync(
                    userToken = userToken,
                    productId = productId,
                    count = count
                ).await()
                if(result.result == "success") updateCartAsync(userToken)
                else {
                    _cartChangingEvent.value = NetworkEvent(State.ERROR, result.error)
                    cartChangesPermitted = true
                }
            } catch(exc: Exception) {
                _cartChangingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                cartChangesPermitted = true
            }
        }
    }

    fun sendGoToMakeOrderEvent() {
        _goToMakeOrderEvent.value = OneTimeEvent()
    }

    fun makeOrder(
        name: String,
        phone: String,
        email: String,
        city: String,
        street: String,
        house: String,
        flat: String,
        saveDataForFuture: Boolean
    ) {
        viewModelScope.launch {
            _makeOrderEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.makeOrderAsync(
                    userToken = App.getInstance().userToken,
                    name = name,
                    phone = phone,
                    email = email,
                    city = city,
                    street = street,
                    house = house,
                    flat = flat,
                    saveDataForFuture = if(saveDataForFuture) "1" else "0"
                ).await()
                if(result.result == "success") {
                    _makeOrderEvent.value = NetworkEvent(State.SUCCESS)
                    _finishOrderEvent.value = Event(result.orderNumber ?: 0)
                }
                else _makeOrderEvent.value = NetworkEvent(State.ERROR, result.error)
            } catch (exc: Exception) {
                _makeOrderEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }


    private val _productsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val productsLoadingEvent: LiveData<NetworkEvent<State>> = _productsLoadingEvent

    fun loadProducts(userToken: String?, voucherCode: String?) {
        viewModelScope.launch {
            _productsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getProductsAsync(
                    userToken = userToken,
                    voucherCode = voucherCode
                ).await()
                if(response.result == "success") {
                    _productsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                    createShopItemsList(response.products)
                } else {
                    _productsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _productsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _shopItems = MutableLiveData<List<ShopItem>>()
    val shopItems: LiveData<List<ShopItem>> = _shopItems

    private fun createShopItemsList(responseList: List<ProductsResponse.Product>?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mutableListOf<ShopItem>()
            val categories = responseList?.distinctBy { it.categoryId } ?: listOf()
            val products = responseList ?: listOf()
            for(category in categories) {
                val categoryId = UniqueIdGenerator.nextId()
                result.add(ShopCategory(
                    id = categoryId,
                    name = category.category ?: "",
                    selectedProducts = 0,
                    isExpanded = true
                ))
                result.addAll(
                    products.filter { it.categoryId == category.categoryId }
                        .toShopProducts(categoryId)
                )
            }
            _shopItems.postValue(result)
        }
    }

    fun resetShop() {
        _shopItems.value = listOf()
    }

}