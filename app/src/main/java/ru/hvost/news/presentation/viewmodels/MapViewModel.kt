package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.data.api.response.ShopsResponse
import ru.hvost.news.utils.enums.State

class MapViewModel:ViewModel() {

    private val mutableShopsState:MutableLiveData<State> = MutableLiveData()
    val shopsState:LiveData<State> = mutableShopsState

    private val mutableShops:MutableLiveData<ShopsResponse> = MutableLiveData()
    val shops:LiveData<ShopsResponse> = mutableShops

    fun getShops(userToken:String){

    }

}