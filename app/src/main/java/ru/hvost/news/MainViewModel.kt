package ru.hvost.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.UserDataResponse
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State

class MainViewModel : ViewModel() {

    var testPrize: List<Prize> = Prize.getTestPrizeList()
    var testPrice: List<PrizePrice> = PrizePrice.getTestPriceList()

    val articlesState = MutableLiveData<State>()
    val articles = MutableLiveData<List<Article>>()
    val allArticles = MutableLiveData<List<Article>>()
    val allArticlesState = MutableLiveData<State>()

    val userDataState = MutableLiveData<State>()
    val userDataResponse = MutableLiveData<UserDataResponse>()
    val changeUserDataState = MutableLiveData<State>()

    val userPetsState = MutableLiveData<State>()
    val userPetsResponse = MutableLiveData<List<Pets>>()

    var categories: List<Categories>? = null
    var domains: List<Domain>? = null

    init {
        loadArticles()
        loadAllArticles()
        loadUserData()
        loadPetsData()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            articlesState.value = State.LOADING
            try {
                val response = APIService.API.getArticlesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    articles.value = response.articles?.toArticles()
                    articlesState.value = State.SUCCESS
                } else articlesState.value = State.ERROR
            } catch (exc: Exception) {
                articlesState.value = State.FAILURE
            }
        }
    }

    private fun loadAllArticles() {
        viewModelScope.launch {
            allArticlesState.value = State.LOADING
            try {
                val response = APIService.API.getArticlesAsync().await()
                if (response.result == "success") {
                    allArticles.value = response.articles?.toArticles()
                    categories = allArticles.value?.toCategory()
                    domains = allArticles.value?.toDomain()
                    allArticlesState.value = State.SUCCESS
                } else allArticlesState.value = State.ERROR
            } catch (exc: Exception) {
                allArticlesState.value = State.FAILURE
            }
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            userDataState.value = State.LOADING
            try {
                val response = APIService.API.getUserDataAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    userDataResponse.value = response
                    userDataState.value = State.SUCCESS
                } else userDataState.value = State.ERROR
            } catch (exc: Exception) {
                userDataState.value = State.FAILURE
            }
        }
    }

    fun loadPetsData() {
        viewModelScope.launch {
            userPetsState.value = State.LOADING
            try {
                val response = APIService.API.getPetsAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    userPetsResponse.value = response.pets?.toPets()
                    userPetsState.value = State.SUCCESS
                } else userPetsState.value = State.ERROR
            } catch (exc: Exception) {
                userPetsState.value = State.FAILURE
            }
        }
    }

    fun changeUserData(
        name: String?,
        surname: String?,
        patronymic: String?,
        phone: String?,
        email: String?,
        birthday: String?,
        city: String?,
        interests: List<String>? = null
    ) {
        viewModelScope.launch {
            changeUserDataState.value = State.LOADING
            try {
                val response = APIService.API.getUpdateUserProfileAsync(
                    userToken = App.getInstance().userToken,
                    name = name,
                    surname = surname,
                    patronymic = patronymic,
                    phone = phone,
                    email = email,
                    birthday = birthday,
                    city = city,
                    interests = interests
                ).await()
                if (response.result == "success") {
                    changeUserDataState.value = State.SUCCESS
                } else changeUserDataState.value = State.ERROR
            } catch (exc: Exception) {
                changeUserDataState.value = State.FAILURE
            }
        }
    }

}