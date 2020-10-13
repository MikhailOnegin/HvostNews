package ru.hvost.news

import android.app.Application
import androidx.preference.PreferenceManager
import java.lang.IllegalStateException

class App : Application() {

    var userToken: String? = null
        private set

    fun logIn(userToken: String) {
        if(this.userToken != null) throw RuntimeException("Don't try to change userToken!")
        this.userToken = userToken
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putString(PREF_USER_TOKEN, userToken).apply()
    }

    fun logOut() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().remove(PREF_USER_TOKEN).apply()
        userToken = null
    }

    override fun onCreate() {
        super.onCreate()
        reference = this
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val userToken = prefs.getString(PREF_USER_TOKEN, null)
        if(userToken != null) logIn(userToken)
    }

    companion object {

        private var reference: App? = null

        fun getInstance(): App {
            return reference ?: throw IllegalStateException("App is not initialized.")
        }

        const val PREF_USER_TOKEN = "pref_user_token"

    }

}