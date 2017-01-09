package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.activity.LoginActivity
import ru.wutiarn.edustor.android.data.models.OAuthTokenResult
import ru.wutiarn.edustor.android.util.extension.startActivity

class ActiveSession(val edustorPreferences: EdustorPreferences,
                    val context: Context, val syncManager: SyncManager, val pdfSyncManager: PdfSyncManager) {
    var token: String? by edustorPreferences.getDelegate(String::class.java)
    var refreshToken: String? by edustorPreferences.getDelegate(String::class.java)

    val isLoggedIn: Boolean
        get() = token != null

    fun setFromOAuthTokenResult(result: OAuthTokenResult) {
        token = result.token
        result.refreshToken?.let { refreshToken = it }
    }

    fun logout() {
        token = null
        refreshToken = null
        syncManager.syncEnabled = false
        edustorPreferences.clear()
        Realm.getDefaultInstance().use { it.executeTransaction(Realm::deleteAll) }
        context.startActivity(LoginActivity::class.java, true)
    }
}
