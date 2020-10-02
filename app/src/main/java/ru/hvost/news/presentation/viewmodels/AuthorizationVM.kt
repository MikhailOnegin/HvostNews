package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.utils.enums.State

class AuthorizationVM: ViewModel(){

    private val _loginState = MutableLiveData<State>()
    val loginState: LiveData<State> = _loginState

    fun logIn(login: String, password: String){
        viewModelScope.launch {
            _loginState.value = State.LOADING
            try{
                val response = APIService.API.loginAsync(login, password).await()
                if(response.result == "success") _loginState.value = State.SUCCESS
                else _loginState.value = State.ERROR
            }catch (exc: Exception){
                _loginState.value = State.FAILURE
            }
        }
    }

}