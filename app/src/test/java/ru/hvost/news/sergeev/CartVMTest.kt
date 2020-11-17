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
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.rules.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CartVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var cartVM: CartViewModel
    private val correctUserToken = "eyJpdiI6IlhBPT0iLCJ2YWx1ZSI6Ik1kTTVrc3FMTktRQmlnVFlOZnE5NnlBK2xrdGVESTR4ekhBdVg0VjlMaWs9IiwicGFzc3dvcmQiOiJLR3hUTlhrOE9VTTFPR00wWXpVMlpXSmtaRFExTlRVMFl6TmlaR05sWXpBM09UY3lObUl4TWc9PSJ9"
    private val incorrectUserToken = "chop-chop"

    @Before
    fun setup() {
        cartVM = CartViewModel()
    }

    @Test
    fun updateCart_worksCorrect_withCorrectCredential() = coroutineRule.testDispatcher.runBlockingTest {
        cartVM.updateCartAsync(correctUserToken)
        val result = cartVM.cartUpdateEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun updateCart_worksCorrect_withIncorrectCredential() = coroutineRule.testDispatcher.runBlockingTest {
        cartVM.updateCartAsync(incorrectUserToken)
        val result = cartVM.cartUpdateEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }


}