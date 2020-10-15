package ru.hvost.news.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValueTest
import junit.framework.TestCase.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.data.api.APIService
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.rules.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SchoolViewModelTest {

    private val timeout = 10000L
    private val userToken = "eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ=="
    private val city = "Новосибирск"

    private lateinit var schoolVmTest: SchoolViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        schoolVmTest = SchoolViewModel()
    }

    @Test
    fun getOfflineLessons() = coroutineRule.testDispatcher.runBlockingTest {
        schoolVmTest.getOfflineLessons(city)
        val result = schoolVmTest.offlineLessonsState.getOrAwaitValueTest(
            time = timeout,
            condition = { t: State? -> t != State.LOADING }
        )
        assertEquals("Ошибка загрузки оффлайн уроков", State.SUCCESS, result)
    }

    @Test
    fun getOnlineLessons() = coroutineRule.testDispatcher.runBlockingTest {
        schoolVmTest.getOnlineLessons(userToken)
        val result = schoolVmTest.onlineLessonsState.getOrAwaitValueTest(
            time = timeout,
            condition = { t: State? -> t != State.LOADING }
        )
        assertEquals("Ошибка загрузки онлайн уроков", State.SUCCESS, result)
    }

    @Test
    fun getOnlineSchools() = coroutineRule.testDispatcher.runBlockingTest {
        schoolVmTest.getOnlineSchools(userToken)
        val result = schoolVmTest.onlineSchoolsState.getOrAwaitValueTest(
            time = timeout,
            condition = { t: State? -> t != State.LOADING }
        )
        assertEquals("Ошибка загрузки онлайн уроков", State.SUCCESS, result)
    }

}