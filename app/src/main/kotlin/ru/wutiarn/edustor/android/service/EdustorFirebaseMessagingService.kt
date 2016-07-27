package ru.wutiarn.edustor.android.service

import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R

class EdustorFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.i("FirebaseReceiver", "Received: ${msg.data}")

        val application = application as EdustorApplication
        when (msg.data["command"]) {
            "sync" -> {
                val appComponent = application.appComponent
                ContentResolver.setSyncAutomatically(appComponent.constants.syncAccount, getString(R.string.authority), true)
                ContentResolver.requestSync(appComponent.constants.syncAccount, getString(R.string.authority), Bundle())
            }
        }
    }
}
