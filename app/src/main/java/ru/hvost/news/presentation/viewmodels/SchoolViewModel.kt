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
import ru.hvost.news.utils.events.NetworkEvent

class SchoolViewModel: ViewModel() {

    private val mutableOfflineLessonsState:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineLessonsState:LiveData<NetworkEvent<State>> = mutableOfflineLessonsState

    private val mutableOfflineSeminars:MutableLiveData<OfflineSeminars> = MutableLiveData()
    val offlineSeminars:LiveData<OfflineSeminars> = mutableOfflineSeminars

    fun getOfflineSeminars(cityId:String, userToken: String){
        viewModelScope.launch {
            mutableOfflineLessonsState.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOfflineSeminarsAsync(cityId, userToken).await()
                mutableOfflineSeminars.value = response.toOfflineLessons()
                if (response.result == "success") mutableOfflineLessonsState.value = NetworkEvent(State.SUCCESS)
                else mutableOfflineLessonsState.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                mutableOfflineLessonsState.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val mutableOnlineLessonsState:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineLessonsState:LiveData<NetworkEvent<State>> = mutableOnlineLessonsState

    private val mutableOnlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = mutableOnlineLessons

    fun getOnlineLessons(userToken:String, schoolId:String){
        //viewModelScope.launch {
        //    mutableOnlineLessonsState.value = State.LOADING
        //    try {
        //        val response = APIService.API.getOnlineLessonsAsync(userToken,schoolId).await()
        //        mutableOnlineLessons.value = response.toOfflineLessons()
        //        if (response.result == "success") mutableOnlineLessonsState.value = NetworkEvent(State.SUCCESS)
        //        else mutableOnlineLessonsState.value = NetworkEvent(State.ERROR, response.error)
        //    } catch (exc: Exception) {
        //        mutableOnlineLessonsState.value = NetworkEvent(State.FAILURE, exc.toString())
        //    }
        //}
        val onlineLessons = mutableListOf<OnlineLessons.OnlineLesson>()
        val answerList = mutableListOf<OnlineLessons.Answer>()

        answerList.add(
            OnlineLessons.Answer(
                "Чёрт",
                false
            ))
        answerList.add(
            OnlineLessons.Answer(
                "Программист",
                false
            ))
        answerList.add(
            OnlineLessons.Answer(
                "Служивый",
                false
            ))
        answerList.add(
            OnlineLessons.Answer(
                "Репитлоид",
                true
            ))
        for(i in 0 .. 10){
            val isFinished = i<4
            onlineLessons.add(
                OnlineLessons.OnlineLesson(
                    i,
                    (123 + i).toString(),
                    "Как подобрать корм",
                    "1.5 года",
                    69,
                    isFinished,
                    "https://www.youtube.com/watch?v=GRkStJ8BxzQ&ab_channel=%D0%9D%D0%B0%D0%B2%D0%B8%D0%B3%D0%B0%D1%82%D0%BE%D1%80%D0%B8%D0%B3%D1%80%D0%BE%D0%B2%D0%BE%D0%B3%D0%BE%D0%BC%D0%B8%D1%80%D0%B0",
                    "Кто по жизни?",
                    answerList
                )
            )
        }
        val onlineLessons2 = OnlineLessons(onlineLessons)
        mutableOnlineLessons.value = onlineLessons2
    }

    private val mutableOnlineSchoolsState:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineSchoolsState:LiveData<NetworkEvent<State>> = mutableOnlineSchoolsState

    private val mutableOnlineSchools:MutableLiveData<OnlineSchools> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchools> = mutableOnlineSchools

    fun getOnlineSchools(userToken: String){
        //viewModelScope.launch {
        //    mutableOnlineSchoolsState.value = NetworkEvent(State.LOADING)
        //    try {
        //        val response = APIService.API.getOnlineSchoolsAsync(userToken).await()
        //        mutableOnlineSchools.value = response.toOnlineSchools()
        //        if (response.result == "success") mutableOnlineSchoolsState.value = NetworkEvent(State.SUCCESS)
        //        else mutableOnlineSchoolsState.value = NetworkEvent(State.ERROR, response.error)
        //    } catch (exc: Exception) {
        //        Log.i("eeee", " getOnlineSchools() ERROR: ${exc.message.toString()}")
        //        mutableOnlineSchoolsState.value = NetworkEvent(State.FAILURE, exc.toString())
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
                    "https://www.ozon.ru/context/detail/id/147372948/?utm_source=google&utm_medium=cpc&utm_campaign=RF_Product_Shopping_Books_newclients&gclid=Cj0KCQiAzZL-BRDnARIsAPCJs72eVknuneOeB2yBjvzq8h-6SrmXzQlAAPvhmmUU8JUJmhZIjL_8LnQaAqCuEALw_wcB"
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
            val isRegistered = i>4
            onlineSchools11.add(
                OnlineSchools.OnlineSchool(
                    i,
                    isRegistered,
                    id.toLong(),
                    "Онлайн-школа для владельцев щенков",
                    "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
                    "Новичок",
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

    private val mutableSetLessonTestesPassedState:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val lessonTestesPassedState:LiveData<NetworkEvent<State>> = mutableSetLessonTestesPassedState

    private val mutableSetLessonTestesPassed:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val lessonTestesPassed:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassed

    fun setLessonTestesPassed(userToken:String, lessonId:Long){
        viewModelScope.launch {
            mutableSetLessonTestesPassedState.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.setLessonTestesPassedAsync(userToken, lessonId).await()
                mutableSetLessonTestesPassed.value = response
                if (response.result == "success") mutableSetLessonTestesPassedState.value = NetworkEvent(State.SUCCESS)
                else mutableSetLessonTestesPassedState.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                mutableSetLessonTestesPassedState.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val mutableOfflineCitiesState:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineCitiesState:LiveData<NetworkEvent<State>> = mutableOfflineCitiesState

    private val mutableOfflineCities:MutableLiveData<CitiesOffline> = MutableLiveData()
    val offlineCities:LiveData<CitiesOffline> = mutableOfflineCities

    fun getOfflineCities(){
        viewModelScope.launch {
            mutableOfflineCitiesState.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOfflineCitiesAsync().await()
                mutableOfflineCities.value = response.toOfflineLessons()
                if (response.result == "success") mutableOfflineCitiesState.value = NetworkEvent(State.SUCCESS)
                else mutableOfflineCitiesState.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                mutableOfflineCitiesState.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }
}