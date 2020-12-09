package ru.hvost.news.presentation.fragments.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.hvost.news.utils.events.OneTimeEvent

class SplashViewModel : ViewModel() {

    private val _splashFinishEvent = MutableLiveData<OneTimeEvent>()
    val splashFinishEvent: LiveData<OneTimeEvent> = _splashFinishEvent

    fun startSplashScreenTimer(timeMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(timeMillis)
            _splashFinishEvent.postValue(OneTimeEvent())
        }
    }

    fun dismissSplashScreen() {
        _splashFinishEvent.value = OneTimeEvent()
    }

}