package ru.hvost.news

import android.app.Application
import androidx.preference.PreferenceManager
import java.lang.IllegalStateException

class App : Application() {

    lateinit var userToken: String private set
    var isTokenInitialized = false
        private set

    fun setUserToken(userToken: String){
        if(this::userToken.isInitialized) throw RuntimeException("Don't try to change userToken!")
        this.userToken = userToken
        isTokenInitialized = true
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putString(PREF_USER_TOKEN, userToken).apply()
    }

    override fun onCreate() {
        super.onCreate()
        reference = this
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val userToken = prefs.getString(PREF_USER_TOKEN, null)
        if(userToken != null) setUserToken(userToken)
    }

    companion object {

        private var reference: App? = null

        fun getInstance(): App {
            return reference ?: throw IllegalStateException("App is not initialized.")
        }

        const val PREF_USER_TOKEN = "pref_user_token"

    }

}