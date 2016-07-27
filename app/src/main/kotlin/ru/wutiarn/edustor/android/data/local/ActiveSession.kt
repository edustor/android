package ru.wutiarn.edustor.android.data.local

import android.content.Context
import ru.wutiarn.edustor.android.activity.LoginActivity
import ru.wutiarn.edustor.android.util.extension.startActivity

class ActiveSession(val edustorPreferences: EdustorPreferences, val context: Context) {
    var token: String?
        get() = edustorPreferences.token
        set(value) {
            edustorPreferences.token = value
        }

    val isLoggedIn: Boolean
        get() = token != null

    fun logout() {
        token = null
        context.startActivity(LoginActivity::class.java, true)
    }
}
