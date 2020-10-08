package ru.hvost.news.presentation.fragments.login

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
import ru.hvost.news.MainCoroutineRule
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.utils.enums.State

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthorizationVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private val timeout = 10L
    private lateinit var authorizationVM: AuthorizationVM

    @Before
    fun setup(){
        authorizationVM = AuthorizationVM()
    }

    @Test
    fun login_wrongCredentials_setsLoginStateToError() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.logIn("wrongLogin", "wrongPassword")
        val result = authorizationVM.loginState.getOrAwaitValue(timeout, attempts = 2)
        assertThat(result, `is`(State.ERROR))
    }

    @Test
    fun login_correctCredentials_setsLoginStateToSuccess() = runBlockingTest {
        authorizationVM.logIn("test", "123123123")
        val result = authorizationVM.loginState.getOrAwaitValue(timeout, attempts = 2)
        assertThat(result, `is`(State.SUCCESS))
    }

}