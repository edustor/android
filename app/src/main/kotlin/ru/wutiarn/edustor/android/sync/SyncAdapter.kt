package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.os.Handler
import android.util.Log
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.util.extension.syncNow

class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {

    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?) {
        Log.i("SyncAdapter", "Edustor is syncing")
        Handler(context.mainLooper).post {
            context.makeToast("Edustor is syncing")
        }
        val appComponent = context.initializeNewAppComponent()
        appComponent.syncApi.syncNow().subscribe()
    }

}