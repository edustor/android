package ru.wutiarn.edustor.android.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

class EdustorFirebaseInstanceIdService : FirebaseInstanceIdService() {

    lateinit var prefs: EdustorPreferences

    override fun onCreate() {
        prefs = EdustorPreferences(applicationContext)
    }

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        prefs.firebaseToken = token
        Log.i("FirebaseIdService", "Token changed: $token")
    }
}
