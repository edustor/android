package ru.wutiarn.edustor.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.wutiarn.edustor.android.EdustorApplication

class EdustorFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.i("FirebaseReceiver", "Received: ${msg.data}")

        val application = application as EdustorApplication
        val appComponent = application.appComponent
        when (msg.data["command"]) {
            "sync" -> {
                appComponent.syncManager.requestSync(false, false)
            }
        }
    }
}
