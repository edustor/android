package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.activity.LoginActivity
import ru.wutiarn.edustor.android.util.extension.startActivity

class ActiveSession(val edustorPreferences: EdustorPreferences,
                    val context: Context, val syncManager: SyncManager, val pdfSyncManager: PdfSyncManager) {
    var token: String?
        get() = edustorPreferences.token
        set(value) {
            edustorPreferences.token = value
        }

    val isLoggedIn: Boolean
        get() = token != null

    fun logout() {
        token = null
        syncManager.syncEnabled = false
        pdfSyncManager.syncEnabled = false
        edustorPreferences.clear()
        Realm.getDefaultInstance().use { it.executeTransaction { it.deleteAll() } }
        context.startActivity(LoginActivity::class.java, true)
    }
}
