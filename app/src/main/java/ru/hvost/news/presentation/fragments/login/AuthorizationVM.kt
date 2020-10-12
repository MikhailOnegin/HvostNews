package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.utils.Event
import ru.hvost.news.utils.enums.State

class AuthorizationVM: ViewModel(){

    private val _loginEvent = MutableLiveData<Event<State>>()
    val loginEvent: LiveData<Event<State>> = _loginEvent

    fun logIn(login: String, password: String){
        viewModelScope.launch {
            _loginEvent.value = Event(State.LOADING)
            try{
                val response = APIService.API.loginAsync(login, password).await()
                if(response.result == "success" && response.userToken != null) {
                    _loginEvent.value = Event(State.SUCCESS)
                    App.getInstance().setUserToken(response.userToken)
                }
                else _loginEvent.value = Event(State.ERROR)
            }catch (exc: Exception){
                _loginEvent.value = Event(State.FAILURE)
            }
        }
    }

}