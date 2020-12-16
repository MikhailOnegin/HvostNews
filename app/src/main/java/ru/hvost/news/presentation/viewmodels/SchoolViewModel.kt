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

    private val _offlineSeminarsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineSeminarsEvent:LiveData<NetworkEvent<State>> = _offlineSeminarsEvent

    private val _offlineSeminars:MutableLiveData<OfflineSeminars> = MutableLiveData()
    val offlineSeminars:LiveData<OfflineSeminars> = _offlineSeminars

    fun getOfflineSeminars(cityId:String, userToken: String){
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



    private val _onlineLessonsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineLessonsEvent:LiveData<NetworkEvent<State>> = _onlineLessonsEvent

    private val _onlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = _onlineLessons

    fun getOnlineLessons(userToken:String, schoolId:String){
        viewModelScope.launch {
            _onlineLessonsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOnlineLessonsAsync(userToken,schoolId).await()
                _onlineLessons.value = response.toOfflineLessons()
                if (response.result == "success") _onlineLessonsEvent.value = NetworkEvent(State.SUCCESS)
                else _onlineLessonsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _onlineLessonsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
       // val onlineLessons = mutableListOf<OnlineLessons.OnlineLesson>()
       // val answerList = mutableListOf<OnlineLessons.Answer>()
//
       // answerList.add(
       //     OnlineLessons.Answer(
       //         "Чёрт",
       //         false
       //     ))
       // answerList.add(
       //     OnlineLessons.Answer(
       //         "Программист",
       //         false
       //     ))
       // answerList.add(
       //     OnlineLessons.Answer(
       //         "Служивый",
       //         false
       //     ))
       // answerList.add(
       //     OnlineLessons.Answer(
       //         "Репитлоид",
       //         true
       //     ))
       // for(i in 0 .. 10){
       //     val isFinished = i<4
       //     onlineLessons.add(
       //         OnlineLessons.OnlineLesson(
       //             i,
       //             (123 + i).toString(),
       //             "Как подобрать корм",
       //             "1.5 года",
       //             69,
       //             isFinished,
       //             "https://www.youtube.com/watch?v=GRkStJ8BxzQ&ab_channel=%D0%9D%D0%B0%D0%B2%D0%B8%D0%B3%D0%B0%D1%82%D0%BE%D1%80%D0%B8%D0%B3%D1%80%D0%BE%D0%B2%D0%BE%D0%B3%D0%BE%D0%BC%D0%B8%D1%80%D0%B0",
       //             "Кто по жизни?",
       //             answerList
       //         )
       //     )
       // }
       // val onlineLessons2 = OnlineLessons(onlineLessons)
       // mutableOnlineLessons.value = onlineLessons2
    }

    private val _onlineSchoolsEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val onlineSchoolsEvent:LiveData<NetworkEvent<State>> = _onlineSchoolsEvent

    private val _onlineSchools:MutableLiveData<OnlineSchools> = MutableLiveData()
    val onlineSchools:LiveData<OnlineSchools> = _onlineSchools

    fun getOnlineSchools(userToken: String){
        viewModelScope.launch {
            _onlineSchoolsEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getOnlineSchoolsAsync(userToken).await()
                _onlineSchools.value = response.toOnlineSchools()
                if (response.result == "success") _onlineSchoolsEvent.value = NetworkEvent(State.SUCCESS)
                else _onlineSchoolsEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                Log.i("eeee", " getOnlineSchools() ERROR: ${exc.message.toString()}")
                _onlineSchoolsEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
        //val onlineSchools11 = mutableListOf<OnlineSchools.OnlineSchool>()
        //val literatures = mutableListOf<OnlineSchools.Literature>()
        //val lessonPassed = mutableListOf<OnlineSchools.LessonPassed>()
        //val waitList = mutableListOf<OnlineSchools.Wait>()
        //for(i in 0 .. 23){
        //    val b = i>4
        //    lessonPassed.add(
        //        OnlineSchools.LessonPassed(
        //            i+1,
        //            b
        //        )
        //    )
        //}
//
        //for (i in 0 .. 10){
        //    literatures.add(
        //        OnlineSchools.Literature(
        //            "Вакцинация",
        //            "Для кота",
        //            "https://www.ozon.ru/context/detail/id/147372948/?utm_source=google&utm_medium=cpc&utm_campaign=RF_Product_Shopping_Books_newclients&gclid=Cj0KCQiAzZL-BRDnARIsAPCJs72eVknuneOeB2yBjvzq8h-6SrmXzQlAAPvhmmUU8JUJmhZIjL_8LnQaAqCuEALw_wcB"
        //        ))
        //}
//
        //for (i in 0 .. 10){
        //    waitList.add(
        //        OnlineSchools.Wait(
        //            "Коснпекты",
        //            "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
        //            "Заметки и лайфхаки от экспертов"
        //        )
        //    )
        //}
//
        //for(i in 0 .. 10){
        //    val id = 17174 + i
        //    val isRegistered = i>4
        //    onlineSchools11.add(
        //        OnlineSchools.OnlineSchool(
        //            i,
        //            isRegistered,
        //            id.toLong(),
        //            "Онлайн-школа для владельцев щенков",
        //            "/upload/iblock/a74/shor_shkola273kh211_web.jpg",
        //            "Новичок",
        //            "Это обучающий онлайн-курс в новом формате. Он создан для тех, кто только планирует взять в дом щенка&nbsp;или уже завёл непоседу, и у кого остались вопросы по его содержанию. Мы поможем вам стать суперхозяином и расскажем: почему щенок грызет всё подряд, как подготовиться к первой прогулке и какие этапы взросления ждут малыша. В курсе&nbsp;8 серий по 10-20&nbsp;минут, которые можно просматривать в комфортном&nbsp;темпе и в удобное для вас время",
        //            literatures,
        //            lessonPassed,
        //            waitList
        //        )
        //    )
        //}
        //val onlineSchools21 = OnlineSchools(onlineSchools11)
        //mutableOnlineSchools.value = onlineSchools21
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

    private val _offlineCitiesEvent:MutableLiveData<NetworkEvent<State>> = MutableLiveData()
    val offlineCitiesEvent:LiveData<NetworkEvent<State>> = _offlineCitiesEvent

    private val mutableOfflineCities:MutableLiveData<CitiesOffline> = MutableLiveData()
    val offlineCities:LiveData<CitiesOffline> = mutableOfflineCities

    fun getOfflineCities(){
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
}