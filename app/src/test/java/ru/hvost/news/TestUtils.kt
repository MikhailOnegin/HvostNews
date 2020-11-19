package ru.hvost.news

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

const val correctTestUserToken = "eyJpdiI6IkZ3PT0iLCJ2YWx1ZSI6IjFFQVZHXC9oaHRvXC9lc3RMYjRtY3VVSlh1V0NPZHN6aUtmXC9SeUdjdFRveXM9IiwicGFzc3dvcmQiOiJSbVZMWkV4d2RHaG1ZMlF5WTJNM01UZG1NamN6WWpsaFptSTJNV0V6WldKak5ERm1Nak14WXc9PSJ9"
const val inCorrectTestUserToken = "chop-chop"

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 10,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    attempts: Int = 1
): T {
    var data: T? = null
    val latch = CountDownLatch(attempts)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            if(latch.count == 0L){
                this@getOrAwaitValue.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)
    try {
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}