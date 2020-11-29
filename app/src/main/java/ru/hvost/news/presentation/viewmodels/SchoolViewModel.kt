package ru.hvost.news.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.LessonTestesPassedResponse
import ru.hvost.news.data.api.response.OnlineSchoolsResponse
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonsState:MutableLiveData<State> = MutableLiveData()
    val offlineLessonsState:LiveData<State> = mutableOfflineLessonsState

    private val mutableOfflineSeminars:MutableLiveData<OfflineSeminars> = MutableLiveData()
    val offlineSeminars:LiveData<OfflineSeminars> = mutableOfflineSeminars

    fun getOfflineSeminars(cityId:String){
        viewModelScope.launch {
            mutableOfflineLessonsState.value = State.LOADING
            try {
                val response = APIService.API.getOfflineSeminarsAsync(cityId).await()
                mutableOfflineSeminars.value = response.toOfflineLessons()
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
                mutableOnlineLessons.value = response.toOfflineLessons()
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

    private val mutableOnlineSchools:MutableLiveData<OnlineSchools> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchools> = mutableOnlineSchools

    fun getOnlineSchools(userToken: String){
       //viewModelScope.launch {
       //    mutableOnlineSchoolsState.value = State.LOADING
       //    try {
       //        val response = APIService.API.getOnlineSchoolsAsync(userToken).await()
       //        mutableOnlineSchools.value = response.toOnlineSchools()
       //        if (response.result == "success") mutableOnlineSchoolsState.value = State.SUCCESS
       //        else mutableOnlineSchoolsState.value = State.ERROR
       //    } catch (exc: Exception) {
       //        Log.i("eeee", " getOnlineSchools() ERROR: ${exc.message.toString()}")
       //        mutableOnlineSchoolsState.value = State.FAILURE
       //    }
       //}
        val onlineSchools11 = mutableListOf<OnlineSchools.OnlineSchool>()
        val literatures = mutableListOf<OnlineSchools.Literature>()
        val lessonPassed = mutableListOf<OnlineSchools.LessonPassed>()
        val waitList = mutableListOf<OnlineSchools.Wait>()
        for(i in 0 .. 23){
            val b = i>4
            lessonPassed.add(
                OnlineSchools.LessonPassed(
                    i+1,
                    b
                )
            )
        }

        for (i in 0 .. 10){
            literatures.add(
                OnlineSchools.Literature(
                "Вакцинация",
                "Для кота",
                "src"
            ))
        }

        for (i in 0 .. 10){
            waitList.add(
                OnlineSchools.Wait(
                    "Коснпекты",
                    "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
                    "Заметки и лайфхаки от экспертов"
                )
            )
        }

        for(i in 0 .. 10){
            val id = 17174 + i
            onlineSchools11.add(
                OnlineSchools.OnlineSchool(
                    i,
                    id.toLong(),
                    "Онлайн-школа для владельцев щенков",
                    "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
                    "Высокий ранг",
                    "Это обучающий онлайн-курс в новом формате. Он создан для тех, кто только планирует взять в дом щенка&nbsp;или уже завёл непоседу, и у кого остались вопросы по его содержанию. Мы поможем вам стать суперхозяином и расскажем: почему щенок грызет всё подряд, как подготовиться к первой прогулке и какие этапы взросления ждут малыша. В курсе&nbsp;8 серий по 10-20&nbsp;минут, которые можно просматривать в комфортном&nbsp;темпе и в удобное для вас время",
                    literatures,
                    lessonPassed,
                    waitList
                )
            )
        }
        val onlineSchools21 = OnlineSchools(onlineSchools11)
        mutableOnlineSchools.value = onlineSchools21
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
    val offlineCities:LiveData<CitiesOffline> = mutableOfflineCities

    fun getOfflineCities(){
        viewModelScope.launch {
            mutableOfflineCitiesState.value = State.LOADING
            try {
                val response = APIService.API.getOfflineCitiesAsync().await()
                mutableOfflineCities.value = response.toOfflineLessons()
                if (response.result == "success") mutableOfflineCitiesState.value = State.SUCCESS
                else mutableOfflineCitiesState.value = State.ERROR
            } catch (exc: Exception) {
                Log.i("eeee", " getOfflineCities() ERROR: ${exc.message.toString()}")
                mutableOfflineCitiesState.value = State.FAILURE
            }
        }
    }
}