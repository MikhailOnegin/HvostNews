package ru.hvost.news.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValueTest2
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.presentation.viewmodels.MapViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.rules.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    private val timeout = 10000L
    private val userToken = "eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ=="

    private lateinit var mapVmTest: MapViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        mapVmTest = MapViewModel()
    }

    @Test
    fun getShops()  = coroutineRule.testDispatcher.runBlockingTest {
        mapVmTest.getShops(userToken)
        val result = mapVmTest.shopsState.getOrAwaitValueTest2(
            time = timeout,
            condition = { t: State? -> t != State.LOADING }
        )
        TestCase.assertEquals("ERROR", State.SUCCESS, result)
    }

}