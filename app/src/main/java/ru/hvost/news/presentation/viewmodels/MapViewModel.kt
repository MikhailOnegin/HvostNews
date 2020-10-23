package ru.hvost.news.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.ShopsResponse
import ru.hvost.news.models.Shops
import ru.hvost.news.models.toDomain
import ru.hvost.news.utils.enums.State

class MapViewModel:ViewModel() {

    private val mutableShopsState:MutableLiveData<State> = MutableLiveData()
    val shopsState:LiveData<State> = mutableShopsState

    private val mutableShops:MutableLiveData<Shops> = MutableLiveData()
    val shops:LiveData<Shops> = mutableShops

    fun getShops(userToken:String){
        viewModelScope.launch {
            mutableShopsState.value = State.LOADING
            try {
                val response = APIService.API.getShopsAsync(userToken).await()
                mutableShops.value = response.toDomain()
                if (response.result == "success") mutableShopsState.value = State.SUCCESS
                else mutableShopsState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " getShops() ERROR ${exc.message.toString()}")
                mutableShopsState.value = State.FAILURE
            }
        }
    }

}