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

    private val mutableOfflineSeminars:MutableLiveData<OfflineSeminars> = MutableLiveData()
    val offlineSeminars:LiveData<OfflineSeminars> = mutableOfflineSeminars

    fun getOfflineSeminars(cityId:String){
        //viewModelScope.launch {
        //    mutableOfflineLessonsState.value = State.LOADING
        //    try {
        //        val response = APIService.API.getOfflineSeminarsAsync(cityId).await()
        //        mutableOfflineSeminars.value = response.toOfflineLessons()
        //        if (response.result == "success") mutableOfflineLessonsState.value = State.SUCCESS
        //        else mutableOfflineLessonsState.value = State.ERROR
        //    } catch (exc: Exception) {
        //        Log.i("eeee", "getOfflineLessons() ERROR: ${exc.message}")
        //        mutableOfflineLessonsState.value = State.FAILURE
        //    }
        //}
        val seminars = mutableListOf<OfflineSeminars.OfflineLesson>()
        val videos = mutableListOf<OfflineSeminars.Video>()
        val partners = mutableListOf<OfflineSeminars.Partner>()
        val schedule = mutableListOf<OfflineSeminars.Schedule>()

        for(i in 0 ..10){
            partners.add(
                OfflineSeminars.Partner(
                    "Бетховен",
                    "http://hvost-news.testfact3.ru/upload/iblock/a74/shor_shkola273kh211_web.jpg"
                )
            )
        }
        for( i in 0 .. 10){
            videos.add(
                OfflineSeminars.Video(
                    "Обработка щенков от паразитов",
                    "https://www.youtube.com/watch?v=PyqkKvVZcQQ&ab_channel=%D0%A1%D1%82%D0%B5%D0%BF%D0%B0%D1%88%D0%BA%D0%B8%D0%BD%D0%B4%D0%BE%D0%BC"
                )
            )
        }
        for( i in 0 .. 10){
            schedule.add(
                OfflineSeminars.Schedule(
                    "Обработка от паразитов",
                    "21.10.21",
                    "13:00",
                    "14:00"
                )
            )
        }

        for (i in 0 .. 10){
            val isFinished = i>4
            seminars.add(OfflineSeminars.OfflineLesson(
                i,
                "123 $i",
                "Как подобрать корм",
                "/upload/iblock/f81/273kh211_banner3_web.jpg",
                isFinished,
                "29.02.2020",
                "Новосибирск",
                "Мокрый нос",
                "На что стоит обращать внимание при выборе готовых рационов.<br>\\r\\n • Ошибки и подводные камни при использовании промышленных кормов.<br>\\r\\n • Как профилактировать заболевания выращивания малыша.<br>\\r\\n •&nbsp;Периоды роста и критические этапы развития.<br>",
                isFinished,
                videos,
                partners,
                schedule
            ))
        }
        val offlineSeminars2 = OfflineSeminars(seminars)
        mutableOfflineSeminars.value = offlineSeminars2
    }

    private val mutableOnlineLessonsState:MutableLiveData<State> = MutableLiveData()
    val onlineLessonsState:LiveData<State> = mutableOnlineLessonsState

    private val mutableOnlineLessons:MutableLiveData<OnlineLessons> = MutableLiveData()
    val onlineLessons:LiveData<OnlineLessons> = mutableOnlineLessons

    fun getOnlineLessons(userToken:String, schoolId:String){
        //viewModelScope.launch {
        //    mutableOnlineLessonsState.value = State.LOADING
        //    try {
        //        val response = APIService.API.getOnlineLessonsAsync(userToken,schoolId).await()
        //        mutableOnlineLessons.value = response.toOfflineLessons()
        //        if (response.result == "success") mutableOnlineLessonsState.value = State.SUCCESS
        //        else mutableOnlineLessonsState.value = State.ERROR
        //    } catch (exc: Exception) {
        //        Log.i("eeee", " getOnlineLessons() ERROR: ${exc.message.toString()}")
        //        mutableOnlineLessonsState.value = State.FAILURE
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

    private val mutableSetLessonTestesPassedState:MutableLiveData<State> = MutableLiveData()
    val lessonTestesPassedState:LiveData<State> = mutableSetLessonTestesPassedState

    private val mutableSetLessonTestesPassed:MutableLiveData<LessonTestesPassedResponse> = MutableLiveData()
    val lessonTestesPassed:LiveData<LessonTestesPassedResponse> = mutableSetLessonTestesPassed

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