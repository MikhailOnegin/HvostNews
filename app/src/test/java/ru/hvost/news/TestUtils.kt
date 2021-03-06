package ru.hvost.news

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

const val correctTestUserToken = "eyJpdiI6Ilwvdz09IiwidmFsdWUiOiJGNTdoOVMwV1lyN2xxbXc5M3MwV1d3PT0iLCJwYXNzd29yZCI6IlNWZHBhbGN6UW1KalpHWXlOVEZsTVdReU0yRm1OMkkwWkRRellXWTRZalk1WmpNeVl6UTJaZz09In0="
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