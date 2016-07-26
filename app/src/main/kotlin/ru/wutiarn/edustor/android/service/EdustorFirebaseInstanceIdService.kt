package ru.wutiarn.edustor.android.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class EdustorFirebaseInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.i("FirebaseIdService", "Token changed: $token")
    }
}
