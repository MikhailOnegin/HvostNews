package ru.hvost.news.sergeev

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.getOrAwaitValue
import ru.hvost.news.models.RegInterest
import ru.hvost.news.utils.rules.MainCoroutineRule
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.emptyImageUri

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
    fun loadSpecies_getsListOfSpecies() = coroutineRule.testDispatcher.runBlockingTest {
        registrationVM.loadSpecies()
        val result = registrationVM.species.getOrAwaitValue(attempts = 2)
        assert(!result.isNullOrEmpty())
    }

    @Test
    fun createInterestsIdsWorksCorrectlyWithNullData() = runBlockingTest {
        val data = MutableLiveData<List<RegInterest>>()
        registrationVM.createInterestsIds(data)
        assertThat(registrationVM.interestsIds, `is`(nullValue()))
    }

    @Test
    fun createInterestsIdsWorksCorrectlyWithEmptyList() = runBlockingTest {
        val data = MutableLiveData<List<RegInterest>>()
        data.value = listOf()
        registrationVM.createInterestsIds(data)
        assertThat(registrationVM.interestsIds, `is`(nullValue()))
    }

    @Test
    fun createInterestsIdsWorksCorrectlyWithTwoValues() = runBlockingTest {
        val data = MutableLiveData<List<RegInterest>>()
        data.value = listOf(
            RegInterest(1L, "12", "Chop", emptyImageUri, isSelected = true)
        )
        registrationVM.createInterestsIds(data)
        assertThat(registrationVM.interestsIds, `is`("12"))
    }

    @Test
    fun createInterestsIdsWorksCorrectlyWithManyValues() = runBlockingTest {
        val data = MutableLiveData<List<RegInterest>>()
        data.value = listOf(
            RegInterest(1L, "12", "Chop", emptyImageUri, isSelected = true),
            RegInterest(1L, "7", "Chop", emptyImageUri, isSelected = false),
            RegInterest(1L, "24", "Chop", emptyImageUri, isSelected = true)
        )
        registrationVM.createInterestsIds(data)
        assertThat(registrationVM.interestsIds, `is`("12,24"))
    }

}