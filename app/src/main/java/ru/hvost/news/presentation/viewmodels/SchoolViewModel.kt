package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.data.api.response.OfflineLessonsResponse
import ru.hvost.news.data.api.response.OnlineLessonsResponse
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.utils.enums.State

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonState:LiveData<State> = MutableLiveData()
    val offlineLessonState:LiveData<State> = mutableOfflineLessonState

    private val mutableOfflineLessons:LiveData<OfflineLessonsResponse> = MutableLiveData()
    val offlineLessons:LiveData<OfflineLessonsResponse> = mutableOfflineLessons

    fun getOfflineLessons(city:String){

    }

    private val mutableOnlineLessonsState:LiveData<State> = MutableLiveData()
    val onlineLessonsState:LiveData<State> = mutableOnlineLessonsState

    private val mutableOnlineLessons:LiveData<OnlineLessonsResponse> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessonsResponse> = mutableOnlineLessons

    fun getOnlineLessons(){

    }

    private val mutableOnlineSchoolsState:LiveData<State> = MutableLiveData()
    val onlineSchoolsState:LiveData<State> = mutableOnlineSchoolsState

    private val mutableOnlineSchools:LiveData<OnlineSchoolsResponse> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchoolsResponse> = mutableOnlineSchools

    fun getOnlineSchools(){

    }
}