package ru.hvost.news.presentation.fragments.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.models.CartItem

class CartViewModel : ViewModel() {

    enum class CartType { Products, Prizes }

    val currentCartType = MutableLiveData<CartType>()
    val productsCart = MutableLiveData(CartItem.getTestProductCart())
    val prizesCart = MutableLiveData(CartItem.getTestPrizesCart())

    init {
        currentCartType.value = CartType.Products
    }

}