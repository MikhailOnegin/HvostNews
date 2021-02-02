package ru.hvost.news.services

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.utils.UniqueIdGenerator

class FcmService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putString(PREF_FIREBASE_TOKEN, newToken).apply()
        runBlocking {
            APIService.API.updateFirebaseTokenAsync(
                userToken = App.getInstance().userToken,
                firebaseToken = newToken
            ).await()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean(PREF_SHOW_NOTIFICATIONS, true)) {
            showNotification(message.data)
        }
    }

    private fun showNotification(data: Map<String, String>) {
        val channelId = when (data[KEY_PUSH_TYPE]) {
            PUSH_TYPE_ORDER_STATUS -> MainActivity.ORDER_STATUS_CHANNEL_ID
            PUSH_TYPE_NEW_ARTICLES -> MainActivity.NEW_ARTICLES_CHANNEL_ID
            PUSH_TYPE_OFFLINE_EVENT -> MainActivity.OFFLINE_EVENTS_CHANNEL_ID
            else -> null
        }
        channelId?.let {
            val builder = NotificationCompat.Builder(this, it)
            val intent = Intent(this, MainActivity::class.java).apply {
                //sergeev: Разобраться с флагами.
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            builder.apply {
                setSmallIcon(R.drawable.ic_status_bar_notification)
                setContentTitle(data[KEY_PUSH_TITLE])
                setContentText(data[KEY_PUSH_MESSAGE])
                priority = NotificationCompat.PRIORITY_DEFAULT
                setStyle(NotificationCompat.BigTextStyle().bigText(data[KEY_PUSH_MESSAGE]))
                setAutoCancel(true)
                setContentIntent(pendingIntent)
            }
            with(NotificationManagerCompat.from(this)) {
                notify(UniqueIdGenerator.nextId().toInt(), builder.build())
            }
        }
    }

    @Suppress("unused")
    companion object {

        const val PREF_FIREBASE_TOKEN = "pref_firebase_token"
        const val PREF_SHOW_NOTIFICATIONS = "pref_show_notifications"

        const val TOPIC_NEW_ARTICLES = "TOPIC_NEW_ARTICLES"

        const val PUSH_TYPE_ORDER_STATUS = "PUSH_TYPE_ORDER_STATUS"
        const val PUSH_TYPE_NEW_ARTICLES = "PUSH_TYPE_NEW_ARTICLES"
        const val PUSH_TYPE_OFFLINE_EVENT = "PUSH_TYPE_OFFLINE_EVENT"

        const val KEY_PUSH_TYPE = "pushType"
        const val KEY_PUSH_TITLE = "pushTitle"
        const val KEY_PUSH_MESSAGE = "pushMessage"
        const val KEY_ARTICLE_ID = "articleId"
        const val KEY_ORDER_ID = "orderId"

    }

}