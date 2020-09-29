package ru.hvost.news

import androidx.lifecycle.ViewModel
import ru.hvost.news.models.Article
import ru.hvost.news.models.Domain
import ru.hvost.news.models.toDomain

class MainViewModel : ViewModel() {

    var testList: List<Article> = Article.getTestArticlesList()
    val domains: List<Domain> = testList.toDomain()

}