package ru.hvost.news

import android.app.Application
import java.lang.IllegalStateException

class App : Application() {

    lateinit var userToken: String private set

    fun setUserToken(userToken: String){
        if(this::userToken.isInitialized) throw RuntimeException("Don't try to change userToken!")
        this.userToken = userToken
    }

    override fun onCreate() {
        super.onCreate()
        reference = this
    }

    companion object {

        private var reference: App? = null

        fun getInstance(): App {
            return reference ?: throw IllegalStateException("App is not initialized.")
        }

    }

}