package ru.hvost.news.presentation.fragments.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.isOneOf
import ru.hvost.news.utils.enums.State

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthorizationVMTest {

    private val timeout = 10L

    @get: Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var authorizationVM: AuthorizationVM

    @Before
    fun setup(){
        authorizationVM = AuthorizationVM()
    }

    @Test
    fun login_wrongCredentials_setsLoginStateToError() = runBlockingTest {
        authorizationVM.logIn("wrongLogin", "wrongPassword")
        val result = authorizationVM.loginState.getOrAwaitValue(timeout)
        assertThat(result, `is`(State.ERROR))
    }

    @Test
    fun login_correctCredentials_setsLoginStateToSuccess() = runBlockingTest {
        authorizationVM.logIn("login", "password")
        val result = authorizationVM.loginState.getOrAwaitValue(timeout)
        assertThat(result, `is`(State.SUCCESS))
    }

}