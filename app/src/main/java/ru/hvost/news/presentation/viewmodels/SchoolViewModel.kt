package ru.hvost.news.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LessonTestesPassedResponse
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonsState:MutableLiveData<State> = MutableLiveData()
    val offlineLessonsState:LiveData<State> = mutableOfflineLessonsState

    private val mutableOfflineLessons:MutableLiveData<OfflineLessons> = MutableLiveData()
    val offlineLessons:LiveData<OfflineLessons> = mutableOfflineLessons

    fun getOfflineLessons(cityId:String){
        viewModelScope.launch {
            mutableOfflineLessonsState.value = State.LOADING
            try {
                val response = APIService.API.getOfflineLessonsAsync(cityId).await()
                mutableOfflineLessons.value = response.toDomain()
                if (response.result == "success") mutableOfflineLessonsState.value = State.SUCCESS
                else mutableOfflineLessonsState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", "getOfflineLessons() ERROR: ${exc.message}")
                mutableOfflineLessonsState.value = State.FAILURE
            }
        }
    }

    private val mutableOnlineLessonsState:MutableLiveData<State> = MutableLiveData()
    val onlineLessonsState:LiveData<State> = mutableOnlineLessonsState

    private val mutableOnlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = mutableOnlineLessons

    fun getOnlineLessons(userToken:String, schoolId:String){
        viewModelScope.launch {
            mutableOnlineLessonsState.value = State.LOADING
            try {
                val response = APIService.API.getOnlineLessonsAsync(userToken,schoolId).await()
                mutableOnlineLessons.value = response.toDomain()
                if (response.result == "success") mutableOnlineLessonsState.value = State.SUCCESS
                else mutableOnlineLessonsState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " getOnlineLessons() ERROR: ${exc.message.toString()}")
                mutableOnlineLessonsState.value = State.FAILURE
            }
        }
    }

    private val mutableOnlineSchoolsState:MutableLiveData<State> = MutableLiveData()
    val onlineSchoolsState:LiveData<State> = mutableOnlineSchoolsState

    private val mutableOnlineSchools:MutableLiveData<OnlineSchool> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchool> = mutableOnlineSchools

    fun getOnlineSchools(userToken: String){
        viewModelScope.launch {
            mutableOnlineSchoolsState.value = State.LOADING
            try {
                val response = APIService.API.getOnlineSchoolsAsync(userToken).await()
                mutableOnlineSchools.value = response.toDomain()
                if (response.result == "success") mutableOnlineSchoolsState.value = State.SUCCESS
                else mutableOnlineSchoolsState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " getOnlineSchools() ERROR: ${exc.message.toString()}")
                mutableOnlineSchoolsState.value = State.FAILURE
            }
        }
    }

    private val mutableSetLessonTestesPassedState:MutableLiveData<State> = MutableLiveData()
    val setLessonTestesPassedState:LiveData<State> = mutableSetLessonTestesPassedState

    private val mutableSetLessonTestesPassed:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val setLessonTestesPassed:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassed

    fun setLessonTestesPassed(userToken:String, lessonId:Long){
        viewModelScope.launch {
            mutableSetLessonTestesPassedState.value = State.LOADING
            try {
                val response = APIService.API.setLessonTestesPassedAsync(userToken, lessonId).await()
                mutableSetLessonTestesPassed.value = response
                if (response.result == "success") mutableSetLessonTestesPassedState.value = State.SUCCESS
                else mutableSetLessonTestesPassedState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " setLessonsTestesPassed() ERROR: ${exc.message.toString()}")
                mutableSetLessonTestesPassedState.value = State.FAILURE
            }
        }
    }

    private val mutableOfflineCitiesState:MutableLiveData<State> = MutableLiveData()
    val offlineCitiesState:LiveData<State> = mutableOfflineCitiesState

    private val mutableOfflineCities:MutableLiveData<CitiesOffline> = MutableLiveData()
    private val offlineCities:LiveData<CitiesOffline> = mutableOfflineCities

    fun getOfflineCities(){
        viewModelScope.launch {
            mutableOfflineCitiesState.value = State.LOADING
            try {
                val response = APIService.API.getOfflineCitiesAsync().await()
                mutableOfflineCities.value = response.toDomain()
                if (response.result == "success") mutableOfflineCitiesState.value = State.SUCCESS
                else mutableOfflineCitiesState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " getOfflineCities() ERROR: ${exc.message.toString()}")
                mutableOfflineCitiesState.value = State.FAILURE
            }
        }
    }
}