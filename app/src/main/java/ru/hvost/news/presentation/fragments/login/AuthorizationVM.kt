package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LoginResponse
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent

class AuthorizationVM: ViewModel(){

    private val _loginEvent = MutableLiveData<NetworkEvent<State>>()
    val loginEvent: LiveData<NetworkEvent<State>> = _loginEvent
    var loginResponse: LoginResponse? = null

    fun logIn(login: String, password: String){
        viewModelScope.launch {
            _loginEvent.value = NetworkEvent(State.LOADING)
            try{
                val response = APIService.API.loginAsync(login, password).await()
                if(response.result == "success" && response.userToken != null) {
                    loginResponse = response
                    if (response.isPhoneRegistered == true)
                        App.getInstance().logIn(response.userToken) //sergeev: добавить и после подтверждения номера телефона.
                    _loginEvent.value = NetworkEvent(State.SUCCESS)
                }
                else _loginEvent.value = NetworkEvent(State.ERROR, response.error)
            }catch (exc: Exception){
                _loginEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

    private val _passRestoreEvent = MutableLiveData<NetworkEvent<State>>()
    val passRestoreEvent: LiveData<NetworkEvent<State>> = _passRestoreEvent

    fun restorePassAsync(email: String){
        viewModelScope.launch {
            _passRestoreEvent.value = NetworkEvent(State.LOADING)
            try{
                val response = APIService.API.restorePassAsync(email).await()
                if(response.result == "success") {
                    _passRestoreEvent.value = NetworkEvent(State.SUCCESS)
                }
                else _passRestoreEvent.value = NetworkEvent(State.ERROR, response.error)
            }catch (exc: Exception){
                _passRestoreEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

    private val _readyToSubmitPhone = MutableLiveData(Event(false))
    val readyToSubmitPhone: LiveData<Event<Boolean>> = _readyToSubmitPhone

    fun setReadyToSubmitPhone(ready: Boolean) {
        _readyToSubmitPhone.value = Event(ready)
    }

}