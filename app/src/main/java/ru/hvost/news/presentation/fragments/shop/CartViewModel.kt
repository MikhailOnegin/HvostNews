package ru.hvost.news.presentation.fragments.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.models.CartProduct

class CartViewModel : ViewModel() {

    enum class CartType { Products, Prizes }

    val currentCartType = MutableLiveData<CartType>()
    val productsCart = MutableLiveData(CartProduct.getTestCartProducts())

    init {
        currentCartType.value = CartType.Products
    }

}