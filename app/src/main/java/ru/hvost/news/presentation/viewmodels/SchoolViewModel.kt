package ru.hvost.news.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LessonTestesPassedResponse
import ru.hvost.news.data.api.response.SetParticipateResponse
import ru.hvost.news.data.api.response.SetSubscribeResponse
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent

class SchoolViewModel: ViewModel() {

    val seminarId:MutableLiveData<Long> = MutableLiveData(0)
    val petTypeName:MutableLiveData<String> = MutableLiveData()
    val adapterSeminarsSize:MutableLiveData<Int> = MutableLiveData(0)
    private val _offlineSeminarsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineSeminarsEvent:LiveData<NetworkEvent<State>> = _offlineSeminarsEvent
    private val _offlineSeminars:MutableLiveData<OfflineSeminars> = MutableLiveData()
    val offlineSeminars:LiveData<OfflineSeminars> = _offlineSeminars

    fun getSeminars(cityId:String, userToken: String){
        viewModelScope.launch {
            _offlineSeminarsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOfflineSeminarsAsync(cityId, userToken).await()
                _offlineSeminars.value = response.toOfflineLessons()
                if (response.result == "success") _offlineSeminarsEvent.value = NetworkEvent(State.SUCCESS)
                else _offlineSeminarsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _offlineSeminarsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    val schoolOnlineId:MutableLiveData<Long> = MutableLiveData()
    val selectLessonAnswersCount:MutableLiveData<Int> = MutableLiveData(0)
    private val _onlineLessonsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineLessonsEvent:LiveData<NetworkEvent<State>> = _onlineLessonsEvent
    private val _onlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = _onlineLessons
    private val _lessonReadyEvent = MutableLiveData<OneTimeEvent>()
    val lessonReadyEvent: LiveData<OneTimeEvent> = _lessonReadyEvent

    fun sendLessonReadyEvent() {
        _lessonReadyEvent.value = OneTimeEvent()
    }
    fun getSchoolLessons(userToken:String, schoolId:String){
        viewModelScope.launch {
            _onlineLessonsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOnlineLessonsAsync(userToken,schoolId).await()
                _onlineLessons.value = response.toOfflineLessons()
                if (response.result == "success") {
                    _onlineLessonsEvent.value = NetworkEvent(State.SUCCESS)}
                else _onlineLessonsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _onlineLessonsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    val enabledSchoolRegister:MutableLiveData<Boolean> = MutableLiveData(false)
    val enabledSeminarRegister:MutableLiveData<Boolean> = MutableLiveData(false)
    val successRegistration:MutableLiveData<Boolean> = MutableLiveData(false)
    private val _setParticipateEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val setParticipateEvent:LiveData<NetworkEvent<State>> = _setParticipateEvent
    private val _setParticipate:MutableLiveData<SetParticipateResponse> = MutableLiveData()
    val setParticipate:LiveData<SetParticipateResponse> = _setParticipate

    fun setParticipate(userToken:String, schoolId:String, petId:String?){
        viewModelScope.launch {
            _setParticipateEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.setParticipateAsync(userToken,schoolId, petId).await()
                _setParticipate.value = response
                if (response.result == "success") _setParticipateEvent.value = NetworkEvent(State.SUCCESS)
                else _setParticipateEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _setParticipateEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    val filterSchools:MutableLiveData<String> = MutableLiveData()
    val adapterSchoolsSize: MutableLiveData<Int> = MutableLiveData(0)

    private val _onlineSchoolsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineSchoolsEvent:LiveData<NetworkEvent<State>> = _onlineSchoolsEvent

    private val _onlineSchools:MutableLiveData<OnlineSchools> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchools> = _onlineSchools

    private val _recyclerSchoolsReadyEvent = MutableLiveData<OneTimeEvent>()
    val recyclerSchoolsReadyEvent: LiveData<OneTimeEvent> = _recyclerSchoolsReadyEvent

    fun sendRecyclerSchoolsReadyEvent() {
        _recyclerSchoolsReadyEvent.value = OneTimeEvent()
    }

    fun getSchools(userToken: String){
        viewModelScope.launch {
            _onlineSchoolsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOnlineSchoolsAsync(userToken).await()
                _onlineSchools.value = response.toOnlineSchools()
                if (response.result == "success") {
                    _onlineSchoolsEvent.value = NetworkEvent(State.SUCCESS)
                }

                else _onlineSchoolsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                Log.i("eeee", " getOnlineSchools() ERROR: ${exc.message.toString()}")
                _onlineSchoolsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }



    private val _lessonTestesPassedEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val lessonTestesPassedEvent:LiveData<NetworkEvent<State>> = _lessonTestesPassedEvent

    private val mutableSetLessonTestesPassed:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val lessonTestesPassed:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassed

    fun setLessonTestesPassed(userToken:String, lessonId:Long){
        viewModelScope.launch {
            _lessonTestesPassedEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.setLessonTestesPassedAsync(userToken, lessonId).await()
                mutableSetLessonTestesPassed.value = response
                if (response.result == "success") _lessonTestesPassedEvent.value = NetworkEvent(State.SUCCESS)
                else _lessonTestesPassedEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _lessonTestesPassedEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    val currentCity:MutableLiveData<String> = MutableLiveData()
    val showFinishedSeminars:MutableLiveData<Boolean> = MutableLiveData(true)
    private val _offlineCitiesEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineCitiesEvent:LiveData<NetworkEvent<State>> = _offlineCitiesEvent

    private val mutableOfflineCities:MutableLiveData<CitiesOffline> = MutableLiveData()
    val offlineCities:LiveData<CitiesOffline> = mutableOfflineCities

    private val _recyclerSeminarsReadyEvent = MutableLiveData<OneTimeEvent>()
    val recyclerSeminarsReadyEvent: LiveData<OneTimeEvent> = _recyclerSeminarsReadyEvent

    fun sendRecyclerSeminarsReadyEvent() {
        _recyclerSeminarsReadyEvent.value = OneTimeEvent()
    }

    fun getSeminarsCities(){
        viewModelScope.launch {
            _offlineCitiesEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOfflineCitiesAsync().await()
                mutableOfflineCities.value = response.toOfflineLessons()
                if (response.result == "success") _offlineCitiesEvent.value = NetworkEvent(State.SUCCESS)
                else _offlineCitiesEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _offlineCitiesEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _setSubscribeEvent: MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val setSubscribeEvent:LiveData<NetworkEvent<State>> = _setSubscribeEvent

    fun setSubscribe(userToken: String, schoolId: String){
        viewModelScope.launch {
            _setSubscribeEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.setSubscribeAsync(userToken, schoolId).await()
                if (response.result == "success") _setSubscribeEvent.value = NetworkEvent(State.SUCCESS)
                else _setSubscribeEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _setSubscribeEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }
}