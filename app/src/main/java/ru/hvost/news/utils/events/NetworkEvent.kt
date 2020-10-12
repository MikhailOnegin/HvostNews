package ru.hvost.news.utils.events

class NetworkEvent<T>(
    content: T,
    val error: String? = null
) : Event<T>(content)