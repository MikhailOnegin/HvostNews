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
import ru.hvost.news.correctTestUserToken
import ru.hvost.news.utils.rules.MainCoroutineRule
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.inCorrectTestUserToken
import ru.hvost.news.presentation.fragments.login.AuthorizationVM
import ru.hvost.news.utils.enums.State

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthorizationVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var authorizationVM: AuthorizationVM

    @Before
    fun setup(){
        authorizationVM = AuthorizationVM()
    }

    @Test
    fun logIn_wrongCredentials_setsLoginEventToError() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.logIn("wrongLogin", "wrongPassword")
        val result = authorizationVM.loginEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

    @Test
    fun logIn_correctCredentials_setsLoginEventToSuccess() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.logIn("test@sergeev.ru", "passw0rd")
        val result = authorizationVM.loginEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun restorePass_wrongCredentials_setsRestorePassEventToError() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.restorePassAsync("incorrect.email@mail.kz")
        val result = authorizationVM.passRestoreEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

    @Test
    fun restorePass_correctCredentials_setsRestorePassEventToSuccess() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.restorePassAsync("phoenix.fact@gmail.com")
        val result = authorizationVM.passRestoreEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun requestSms_correctCredentials_setsRequestSmsEventToSuccess() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.requestSms(correctTestUserToken, "79630956722")
        val result = authorizationVM.requestSmsEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun requestSms_incorrectCredentials_setsRequestSmsEventToError() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.requestSms(inCorrectTestUserToken, "796309567")
        val result = authorizationVM.requestSmsEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

    @Test
    fun sendSecretCode_incorrectCredentials_setsSendSecretCodeEventToError() = coroutineRule.testDispatcher.runBlockingTest {
        authorizationVM.sendSecretCode(inCorrectTestUserToken, "796309567", "321242")
        val result = authorizationVM.sendSecretCodeEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

}