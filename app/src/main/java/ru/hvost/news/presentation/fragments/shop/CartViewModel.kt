package ru.hvost.news.presentation.fragments.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.toCartItems
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent
import java.lang.Exception

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
                    //sergeev: Переход к финишу оформления заказа.
                }
                else _makeOrderEvent.value = NetworkEvent(State.ERROR, result.error)
            } catch (exc: Exception) {
                _makeOrderEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

}