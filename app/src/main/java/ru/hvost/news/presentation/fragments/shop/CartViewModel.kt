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
import java.lang.Exception

class CartViewModel : ViewModel() {

    enum class CartType { Products, Prizes }

    val currentCartType = MutableLiveData<CartType>()
    val productsCart = MutableLiveData(listOf<CartItem>())
    val prizesCart = MutableLiveData(listOf<CartItem>())
    @Volatile
    var cartChangesPermitted = false

    init {
        currentCartType.value = CartType.Products
    }

    private val _cartUpdateEvent = MutableLiveData<NetworkEvent<State>>()
    val cartUpdateEvent: LiveData<NetworkEvent<State>> = _cartUpdateEvent

    fun updateCartAsync(userToken: String?) {
        _cartUpdateEvent.value = NetworkEvent(State.LOADING)
        viewModelScope.launch {
            try {
                val result = APIService.API.getCartAsync(userToken).await()
                if(result.result == "success") {
                    productsCart.value = result.toCartItems()
                    _cartUpdateEvent.value = NetworkEvent(State.SUCCESS)
                } else _cartUpdateEvent.value = NetworkEvent(State.ERROR, result.error)
            } catch(exc: Exception) {
                _cartUpdateEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
            cartChangesPermitted = true
        }
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

}