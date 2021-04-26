package ru.hvost.news.presentation.fragments.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.Article
import ru.hvost.news.models.ArticleContent
import ru.hvost.news.models.toArticleContent
import ru.hvost.news.models.toArticles
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent

class ArticleViewModel : ViewModel(){

    private val _loadArticleEvent = MutableLiveData<NetworkEvent<State>>()
    val loadArticleEvent: LiveData<NetworkEvent<State>> = _loadArticleEvent
    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article> = _article

    fun loadArticle(articleId: String?) {
        viewModelScope.launch {
            _loadArticleEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getArticleAsync(
                    userToken = App.getInstance().userToken,
                    articleId = articleId
                ).await()
                if (response.result == "success") {
                    response.article?.run {
                        _article.value = listOf(this).toArticles().firstOrNull()
                    }
                    _loadArticleEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _loadArticleEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _loadArticleEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _shareArticleEvent = MutableLiveData<Event<String>>()
    val shareArticleEvent: LiveData<Event<String>> = _shareArticleEvent

    fun sendShareArticleEvent(articleUrl: String) {
        _shareArticleEvent.value = Event(articleUrl)
    }

    private val _recyclerViewReadyEvent = MutableLiveData<OneTimeEvent>()
    val recyclerViewReadyEvent: LiveData<OneTimeEvent> = _recyclerViewReadyEvent

    fun sendRecyclerViewReadyEvent() {
        _recyclerViewReadyEvent.value = OneTimeEvent()
    }

    private val _articleContent = MutableLiveData<List<ArticleContent>>()
    val articleContent: LiveData<List<ArticleContent>> = _articleContent

    fun processArticle(article: Article?) {
        article?.run {
            viewModelScope.launch(Dispatchers.IO) {
                _articleContent.postValue(toArticleContent())
            }
        }
    }

}