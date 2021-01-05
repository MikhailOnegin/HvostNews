package ru.hvost.news.services

import com.google.firebase.messaging.FirebaseMessagingService

class FcmService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        //sergeev: Сохранять токен на устройстве для последующей передачи на сервер.
    }

}