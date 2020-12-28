package ru.hvost.news.presentation.fragments.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.R
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
                    viewModelScope.launch(Dispatchers.IO) {
                        _shopItems.postValue(uniteShopAndCart())
                    }
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
                if(result.result == "success") {
                    updateCartAsync(userToken)
                    _cartChangingEvent.value = NetworkEvent(State.SUCCESS)
                }
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
    private val _domains = MutableLiveData<List<ShopDomain>>()
    val domains: LiveData<List<ShopDomain>> = _domains
    private var responseDomains: List<ProductsResponse.Domain>? = null

    fun loadProducts(userToken: String?, voucherCode: String?) {
        viewModelScope.launch {
            _productsLoadingEvent.value = NetworkEvent(State.LOADING)
            _domains.value = null
            try {
                val response = APIService.API.getProductsAsync(
                    userToken = userToken,
                    voucherCode = voucherCode
                ).await()
                if(response.result == "success") {
                    _domains.value = response.domains.toShopDomains()
                    responseDomains = response.domains
                    _productsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _domains.value = listOf()
                    responseDomains = null
                    _productsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _domains.value = listOf()
                responseDomains = null
                _productsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _shopItems = MutableLiveData<List<ShopItem>>()
    val shopItems: LiveData<List<ShopItem>> = _shopItems

    fun createShopItemsList(
        domainId: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mutableListOf<ShopItem>()
            val domain = if (domainId == null) responseDomains?.firstOrNull()
                else responseDomains?.firstOrNull{ it.domainId == domainId }
            domain?.run {
                for (category in categories.orEmpty()) {
                    val categoryId = UniqueIdGenerator.nextId()
                    val shopCategory = ShopCategory(
                        id = categoryId,
                        name = category.categoryTitle.orEmpty(),
                        selectedProducts = 0,
                        isExpanded = true
                    )
                    result.add(shopCategory)
                    if (category.products.isNullOrEmpty()) {
                        result.add(ShopMessage(
                            id = UniqueIdGenerator.nextId(),
                            categoryId = categoryId,
                            message = App.getInstance().getString(R.string.shopPreviouslyOrdered)
                        ))
                        shopCategory.isEmpty = true
                    } else {
                        result.addAll(category.products.toShopProducts(categoryId))
                    }
                }
            }
            _shopItems.postValue(uniteShopAndCart(result))
        }
    }

    private fun uniteShopAndCart(shopItems: List<ShopItem>? = null): List<ShopItem> {
        val items = shopItems ?: _shopItems.value ?: listOf()
        val cartItems = productsCart.value?.filterIsInstance<CartProduct>() ?: listOf()
        for(shopItem in items) {
            if(shopItem is ShopCategory) shopItem.selectedProducts = 0
            if(shopItem is ShopProduct) {
                val isInCart = cartItems.firstOrNull {
                    it.productId.toString() == shopItem.productId
                } != null
                shopItem.isInCart = isInCart
                if(isInCart){
                    (items.firstOrNull { it.id == shopItem.categoryId } as ShopCategory)
                        .selectedProducts++
                }
            }
        }
        return items
    }

    fun clearShopItems() {
        _shopItems.value = null
    }

    fun resetShop() {
        _shopItems.value = listOf()
    }

    private val _showAddToCartDialogEvent = MutableLiveData<Event<Long>>()
    val showAddToCartDialogEvent: LiveData<Event<Long>> = _showAddToCartDialogEvent

    fun showAddToCartDialog(productId: Long) {
        _showAddToCartDialogEvent.value = Event(productId)
    }

}