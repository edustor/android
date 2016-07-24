package ru.wutiarn.edustor.android.data.local

import android.content.Intent
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.activity.LoginActivity

class ActiveSession(val edustorPreferences: EdustorPreferences, val application: EdustorApplication) {
    var token: String?
        get() = edustorPreferences.token
        set(value) {
            edustorPreferences.token = value
        }

    val isLoggedIn: Boolean
        get() = token != null

    fun logout() {
        token = null
        val intent = Intent(application, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        application.startActivity(intent)
    }
}
