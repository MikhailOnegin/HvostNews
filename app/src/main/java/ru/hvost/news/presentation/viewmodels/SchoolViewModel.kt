package ru.hvost.news.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.data.api.response.LessonTestesPassedResponse
import ru.hvost.news.data.api.response.OfflineLessonsResponse
import ru.hvost.news.data.api.response.OnlineLessonsResponse
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.utils.enums.State

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonState:MutableLiveData<State> = MutableLiveData()
    val offlineLessonState:LiveData<State> = mutableOfflineLessonState

    private val mutableOfflineLessons:LiveData<OfflineLessonsResponse> = MutableLiveData()
    val offlineLessons:LiveData<OfflineLessonsResponse> = mutableOfflineLessons

    fun getOfflineLessons(city:String){

    }

    private val mutableOnlineLessonsState:MutableLiveData<State> = MutableLiveData()
    val onlineLessonsState:LiveData<State> = mutableOnlineLessonsState

    private val mutableOnlineLessons:MutableLiveData<OnlineLessonsResponse> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessonsResponse> = mutableOnlineLessons

    fun getOnlineLessons(){

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