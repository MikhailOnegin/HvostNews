package ru.hvost.news.models

import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.NotificationPeriodResponse

data class NotificationPeriod(
    val index: Long,
    val periodId: String,
    val value: String
)

fun List<NotificationPeriodResponse.Period>.toNotificationPeriod(): List<NotificationPeriod> {
    val result = mutableListOf<NotificationPeriod>()
    for ((index, period) in this.withIndex()) {
        result.add(
            NotificationPeriod(
                index = index.inc().toLong(),
                periodId = period.periodId ?: "",
                value = period.value ?: "",
            )
        )
    }
    result.add(
        0, NotificationPeriod(
            index = MainViewModel.UNSELECTED_ID,
            periodId = "",
            value = MainViewModel.UNSELECTED
        )
    )
    return result
}