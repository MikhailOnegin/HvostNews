package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LessonTestesPassedResponse
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.models.OfflineLessons
import ru.hvost.news.models.OnlineLessons
import ru.hvost.news.models.toDomain
import ru.hvost.news.utils.enums.State

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonsState:MutableLiveData<State> = MutableLiveData()
    val offlineLessonsState:LiveData<State> = mutableOfflineLessonsState

    private val mutableOfflineLessons:MutableLiveData<OfflineLessons> = MutableLiveData()
    val offlineLessons:LiveData<OfflineLessons> = mutableOfflineLessons

    fun getOfflineLessons(city:String){
        viewModelScope.launch {
            mutableOfflineLessonsState.value = State.LOADING
            try {
                val response = APIService.API.getOfflineLessons(city).await()
                mutableOfflineLessons.value = response.toDomain()
                if (response.result == "success") mutableOfflineLessonsState.value = State.SUCCESS
                else mutableOfflineLessonsState.value = State.ERROR
            } catch (exc: Exception) {
                mutableOfflineLessonsState.value = State.FAILURE
            }
        }
    }

    private val mutableOnlineLessonsState:MutableLiveData<State> = MutableLiveData()
    val onlineLessonsState:LiveData<State> = mutableOnlineLessonsState

    private val mutableOnlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = mutableOnlineLessons

    fun getOnlineLessons(userToken:String){
        viewModelScope.launch {
            mutableOnlineLessonsState.value = State.LOADING
            try {
                val response = APIService.API.getOnlineLessons(userToken).await()
                mutableOnlineLessons.value = response.toDomain()
                if (response.result == "success") mutableOnlineLessonsState.value = State.SUCCESS
                else mutableOnlineLessonsState.value = State.ERROR
            } catch (exc: Exception) {
                mutableOnlineLessonsState.value = State.FAILURE
            }
        }
    }

    private val mutableOnlineSchoolsState:MutableLiveData<State> = MutableLiveData()
    val onlineSchoolsState:LiveData<State> = mutableOnlineSchoolsState

    private val mutableOnlineSchools:MutableLiveData<OnlineSchoolsResponse> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchoolsResponse> = mutableOnlineSchools

    fun getOnlineSchools(){

    }

    private val mutableSetLessonTestesPassedState:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val setLessonTestesPassedState:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassedState

    private val mutableSetLessonTestesPassed:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val setLessonTestesPassed:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassed

    fun setLessonTestesPassed(userToken:String, lessonId:Long){

    }

}