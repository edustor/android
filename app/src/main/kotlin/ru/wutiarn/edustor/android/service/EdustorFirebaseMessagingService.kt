package ru.wutiarn.edustor.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.util.extension.syncNow

class EdustorFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.i("FirebaseReceiver", "Received: ${msg.data}")

        val application = application as EdustorApplication
        when (msg.data["command"]) {
            "sync" -> application.appComponent.syncApi.syncNow().subscribe()
        }
    }
}
