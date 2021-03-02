package ru.hvost.news.data.api.response

data class NotificationPeriodResponse(
    val result: String?,
    val error: String?,
    val periods: List<Period>?
) {
    data class Period(
        val periodId: String?,
        val value: String?
    )
}