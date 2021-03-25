package ru.hvost.news.sergeev

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.correctTestUserToken
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.models.Shop
import ru.hvost.news.presentation.fragments.map.MapFragment
import ru.hvost.news.presentation.fragments.map.MapViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.rules.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MapVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var mapVM: MapViewModel

    @Before
    fun setup() {
        mapVM = MapViewModel()
    }

    @Test
    fun getShops_worksCorrect() = coroutineRule.testDispatcher.runBlockingTest {
        mapVM.loadShops(correctTestUserToken)
        val result = mapVM.shopsLoadingEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun calculateDistanceBetweenTwoShops() {
        val firstShop = Shop(
            id = 0,
            shopId = "",
            latitude = 55.769773,
            37.61936,
            name = "",
            shortDescription = "",
            address = "",
            regime = "",
            phones = listOf(),
            website = "",
            typeShopId = "",
            typeShopName = "",
            promotions = listOf(),
            isFavourite = false
        )
        val secondShop = Shop(
            id = 0,
            shopId = "",
            latitude = 55.770578,
            37.620016,
            name = "",
            shortDescription = "",
            address = "",
            regime = "",
            phones = listOf(),
            website = "",
            typeShopId = "",
            typeShopName = "",
            promotions = listOf(),
            isFavourite = false
        )
        val distance = MapFragment.distanceBetweenTwoShops(firstShop, secondShop)
        val thirdShop = Shop(
            id = 0,
            shopId = "",
            latitude = 45.208602,
            33.35989,
            name = "",
            shortDescription = "",
            address = "",
            regime = "",
            phones = listOf(),
            website = "",
            typeShopId = "",
            typeShopName = "",
            promotions = listOf(),
            isFavourite = false
        )
        val forthShop = Shop(
            id = 0,
            shopId = "",
            latitude = 45.029706,
            35.377569,
            name = "",
            shortDescription = "",
            address = "",
            regime = "",
            phones = listOf(),
            website = "",
            typeShopId = "",
            typeShopName = "",
            promotions = listOf(),
            isFavourite = false
        )
        val secondDistance = MapFragment.distanceBetweenTwoShops(thirdShop, forthShop)
        assert(distance == secondDistance)
    }

}