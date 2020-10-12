package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class AuthorizationVM: ViewModel(){

    private val _loginEvent = MutableLiveData<NetworkEvent<State>>()
    val loginEvent: LiveData<NetworkEvent<State>> = _loginEvent

    fun logIn(login: String, password: String){
        viewModelScope.launch {
            _loginEvent.value = NetworkEvent(State.LOADING)
            try{
                val response = APIService.API.loginAsync(login, password).await()
                if(response.result == "success" && response.userToken != null) {
                    App.getInstance().setUserToken(response.userToken)
                    _loginEvent.value = NetworkEvent(State.SUCCESS)
                }
                else _loginEvent.value = NetworkEvent(State.ERROR, response.error)
            }catch (exc: Exception){
                _loginEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

}