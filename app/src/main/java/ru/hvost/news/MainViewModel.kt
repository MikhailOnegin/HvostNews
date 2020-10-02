package ru.hvost.news

import androidx.lifecycle.ViewModel
import ru.hvost.news.models.*

class MainViewModel : ViewModel() {

    var testList: List<Article> = Article.getTestArticlesList()
    var testPets: List<Pet> = Pet.getTestPetList()
    var testPrize: List<Prize> = Prize.getTestPrizeList()
    var testPrice: List<PrizePrice> = PrizePrice.getTestPriceList()
    val domains: List<Domain> = testList.toDomain()

}