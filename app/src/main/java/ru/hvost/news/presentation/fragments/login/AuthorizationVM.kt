package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LoginResponse
import ru.hvost.news.data.api.response.PassRestoreResponse
import ru.hvost.news.services.FcmService
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent

class AuthorizationVM : ViewModel() {

    private val _loginEvent = MutableLiveData<NetworkEvent<State>>()
    val loginEvent: LiveData<NetworkEvent<State>> = _loginEvent
    var loginResponse: LoginResponse? = null

    fun logIn(login: String, password: String) {
        viewModelScope.launch {
            _loginEvent.value = NetworkEvent(State.LOADING)
            try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance())
                val firebaseToken = prefs.getString(FcmService.PREF_FIREBASE_TOKEN, null)
                val response = APIService.API.loginAsync(login, password, firebaseToken).await()
                if (response.result == "success" && response.userToken != null) {
                    loginResponse = response
                    if (response.isPhoneRegistered == true)
                        App.getInstance().logIn(response.userToken)
                    _loginEvent.value = NetworkEvent(State.SUCCESS)
                } else _loginEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _loginEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

    private val _passRestoreEvent = MutableLiveData<NetworkEvent<State>>()
    val passRestoreEvent: LiveData<NetworkEvent<State>> = _passRestoreEvent
    var passRestoreResponse: PassRestoreResponse? = null

    fun restorePassAsync(email: String) {
        viewModelScope.launch {
            _passRestoreEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.restorePassAsync(email).await()
                if (response.result == "success") {
                    passRestoreResponse = response
                    _passRestoreEvent.value = NetworkEvent(State.SUCCESS)
                } else _passRestoreEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _passRestoreEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

    private val _readyToSubmitPhone = MutableLiveData(Event(false))
    val readyToSubmitPhone: LiveData<Event<Boolean>> = _readyToSubmitPhone

    fun setReadyToSubmitPhone(ready: Boolean) {
        _readyToSubmitPhone.value = Event(ready)
    }

    private val _requestSmsEvent = MutableLiveData<NetworkEvent<State>>()
    val requestSmsEvent: LiveData<NetworkEvent<State>> = _requestSmsEvent

    fun requestSms(userToken: String?, phone: String?) {
        viewModelScope.launch {
            _requestSmsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.requestSMSAsync(
                    userToken = userToken,
                    phone = phone
                ).await()
                if (response.result == "success") {
                    startTimer()
                    _requestSmsEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _requestSmsEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _requestSmsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _sendSecretCodeEvent = MutableLiveData<NetworkEvent<State>>()
    val sendSecretCodeEvent: LiveData<NetworkEvent<State>> = _sendSecretCodeEvent

    fun sendSecretCode(userToken: String?, phone: String?, secretCode: String?) {
        viewModelScope.launch {
            _sendSecretCodeEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.sendSecretCodeAsync(
                    userToken = userToken,
                    phone = phone,
                    secretCode = secretCode
                ).await()
                if (response.result == "success") {
                    _sendSecretCodeEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _sendSecretCodeEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _sendSecretCodeEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _timerTickEvent = MutableLiveData<Event<Int>>()
    val timerTickEvent: LiveData<Event<Int>> = _timerTickEvent

    private fun startTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 120 downTo 0) {
                _timerTickEvent.postValue(Event(i))
                delay(1000L)
            }
        }
    }

}