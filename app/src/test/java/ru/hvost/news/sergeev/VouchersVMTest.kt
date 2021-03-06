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
import ru.hvost.news.presentation.fragments.vouchers.VouchersViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.rules.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class VouchersVMTest {

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var vouchersVM: VouchersViewModel

    @Before
    fun setup() {
        vouchersVM = VouchersViewModel()
    }

    private val correctVoucherCode = "CK0010553MNGDMX65656"
    private val incorrectVoucherCode = "chop"

    @Test
    fun checkVoucher_worksCorrect_withCorrectCredentials() = coroutineRule.testDispatcher.runBlockingTest {
        vouchersVM.checkVoucher(correctVoucherCode)
        val result = vouchersVM.checkVoucherEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.SUCCESS))
    }

    @Test
    fun checkVoucher_worksCorrect_withIncorrectCredentials() = coroutineRule.testDispatcher.runBlockingTest {
        vouchersVM.checkVoucher(incorrectVoucherCode)
        val result = vouchersVM.checkVoucherEvent.getOrAwaitValue(attempts = 2)
        assertThat(result.getContentIfNotHandled(), `is`(State.ERROR))
    }

}