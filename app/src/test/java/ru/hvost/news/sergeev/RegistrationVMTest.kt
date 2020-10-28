package ru.hvost.news.sergeev

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.utils.rules.MainCoroutineRule
import ru.hvost.news.presentation.fragments.login.RegistrationVM

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RegistrationVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var registrationVM: RegistrationVM

    @Before
    fun setup(){
        registrationVM = RegistrationVM()
    }

    @Test
    fun setStage_RegStepUser_setsStageTo33Percents() {
        registrationVM.setStage(RegistrationVM.RegStep.USER)
        assertThat(
            registrationVM.stage.getOrAwaitValue().getContentIfNotHandled(),
            `is`(33)
        )
    }

    @Test
    fun setStage_RegStepPet_setsStageTo66Percents() {
        registrationVM.setStage(RegistrationVM.RegStep.PET)
        assertThat(
            registrationVM.stage.getOrAwaitValue().getContentIfNotHandled(),
            `is`(66)
        )
    }

    @Test
    fun setStage_RegStepInterests_setsStageTo100Percents() {
        registrationVM.setStage(RegistrationVM.RegStep.INTERESTS)
        assertThat(
            registrationVM.stage.getOrAwaitValue().getContentIfNotHandled(),
            `is`(100)
        )
    }

    @Test
    fun loadSpecies_getsListOfSpecies() = coroutineRule.testDispatcher.runBlockingTest {
        registrationVM.loadSpecies()
        val result = registrationVM.species.getOrAwaitValue(attempts = 2)
        assert(!result.isNullOrEmpty())
    }

}