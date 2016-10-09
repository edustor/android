package ru.wutiarn.edustor.android.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.data.local.EdustorPreferences
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask

class EdustorFirebaseInstanceIdService : FirebaseInstanceIdService() {

    lateinit var prefs: EdustorPreferences
    lateinit var syncManager: SyncManager

    override fun onCreate() {
        val appComponent = (application as EdustorApplication).appComponent
        prefs = appComponent.preferences
        syncManager = appComponent.syncManager
    }

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token

        val syncTask = SyncTask("account/FCMToken/put", mapOf(
                "token" to token
        ))
        syncManager.addTask(syncTask)

        Log.i("FirebaseIdService", "Token changed: $token")
    }
}