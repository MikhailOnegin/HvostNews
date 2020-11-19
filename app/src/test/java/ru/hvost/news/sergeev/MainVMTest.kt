package ru.hvost.news.sergeev

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import ru.hvost.news.MainViewModel
import ru.hvost.news.correctTestUserToken
import ru.hvost.news.utils.rules.MainCoroutineRule
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.inCorrectTestUserToken
import ru.hvost.news.utils.enums.State

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var mainVM: MainViewModel

    @Before
    fun setup(){
        mainVM = MainViewModel()
    }

    @Test
    fun updateOrders_withCorrectCredentials_worksCorrect() = coroutineRule.testDispatcher.runBlockingTest {
        mainVM.updateOrders(correctTestUserToken)
        val result = mainVM.loadingOrdersEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun updateOrders_withIncorrectCredentials_worksCorrect() = coroutineRule.testDispatcher.runBlockingTest {
        mainVM.updateOrders(inCorrectTestUserToken)
        val result = mainVM.loadingOrdersEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

    @Test
    fun updateVouchers_withCorrectCredentials_worksCorrect() = coroutineRule.testDispatcher.runBlockingTest {
        mainVM.updateVouchers(correctTestUserToken)
        val result = mainVM.loadingVouchersEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun updateVouchers_withIncorrectCredentials_worksCorrect() = coroutineRule.testDispatcher.runBlockingTest {
        mainVM.updateVouchers(inCorrectTestUserToken)
        val result = mainVM.loadingVouchersEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

}