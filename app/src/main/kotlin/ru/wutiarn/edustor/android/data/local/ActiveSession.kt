package ru.wutiarn.edustor.android.data.local

import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.activity.LoginActivity
import ru.wutiarn.edustor.android.util.extension.startActivity

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
        application.startActivity(LoginActivity::class.java, true)
    }
}
