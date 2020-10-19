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

    var testPets: List<Pet> = Pet.getTestPetList()
    var testPrize: List<Prize> = Prize.getTestPrizeList()
    var testPrice: List<PrizePrice> = PrizePrice.getTestPriceList()

    val articlesState = MutableLiveData<State>()
    val articles = MutableLiveData<List<Article>>()
    val allArticles = MutableLiveData<List<Article>>()
    val allArticlesState = MutableLiveData<State>()

    val userDataState = MutableLiveData<State>()
    val userDataResponse = MutableLiveData<UserDataResponse>()

    var categories: List<Categories>? = null
    var domains: List<Domain>? = null

    init {
        loadArticles()
        loadAllArticles()
        loadUserData()
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

    private fun loadUserData() {
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

}