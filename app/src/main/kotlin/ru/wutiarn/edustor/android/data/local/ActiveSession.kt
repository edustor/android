package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.activity.LoginActivity
import ru.wutiarn.edustor.android.data.models.OAuthTokenResult
import ru.wutiarn.edustor.android.util.extension.startActivity

class ActiveSession(val edustorPreferences: EdustorPreferences,
                    val context: Context, val syncManager: SyncManager, val pdfSyncManager: PdfSyncManager) {
    var token: String? by edustorPreferences.getDelegate(String::class.java)
    var refreshToken: String? by edustorPreferences.getDelegate(String::class.java)
    var expirationDate: Instant? by edustorPreferences.getDelegate(Instant::class.java)

    val isLoggedIn: Boolean
        get() = token != null

    val expired: Boolean
        get() = expirationDate?.let { it > Instant.now() } ?: false

    fun setFromOAuthTokenResult(result: OAuthTokenResult) {
        token = result.token
        expirationDate = Instant.now().plusSeconds(result.expiresIn)
        result.refreshToken?.let { refreshToken = it }
    }

    fun logout() {
        token = null
        refreshToken = null
        expirationDate = null
        syncManager.syncEnabled = false
        pdfSyncManager.syncEnabled = false
        edustorPreferences.clear()
        Realm.getDefaultInstance().use { it.executeTransaction(Realm::deleteAll) }
        context.startActivity(LoginActivity::class.java, true)
    }
}
