package ru.hvost.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State

class MainViewModel : ViewModel() {

    var testPets: List<Pet> = Pet.getTestPetList()
    var testPrize: List<Prize> = Prize.getTestPrizeList()
    var testPrice: List<PrizePrice> = PrizePrice.getTestPriceList()

    val articlesState = MutableLiveData<State>()
    val articles = MutableLiveData<List<Article>>()

    var domains: List<Domain>? = null

    init {
        loadArticles()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            articlesState.value = State.LOADING
            try {
                val response = APIService.API.getArticlesAsync().await()
                if (response.result == "success") {
                    articles.value = response.articles?.toArticles()
                    domains = articles.value?.toDomain()
                    articlesState.value = State.SUCCESS
                } else articlesState.value = State.ERROR
            } catch (exc: Exception) {
                articlesState.value = State.FAILURE
            }
        }
    }

}