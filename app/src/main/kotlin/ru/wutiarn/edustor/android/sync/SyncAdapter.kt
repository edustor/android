package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.os.Handler
import android.util.Log
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.util.extension.fullSyncNow
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent
import ru.wutiarn.edustor.android.util.extension.makeToast
import rx.lang.kotlin.onError

class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {

    val handler = Handler(context.mainLooper)
    val TAG = "SyncAdapter"


    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?) {
        Log.i(TAG, "Edustor is syncing")
        makeToast("Edustor is syncing")

        val appComponent = context.initializeNewAppComponent()
        val tasks = appComponent.syncManager.popAllTasks()

        appComponent.api.sync.push(tasks)
                .onError {
                    Log.w("TAG", "Sync push failed", it)
                    appComponent.syncManager.pushAllTasks(tasks, true)
                }
                .flatMap { appComponent.api.sync.fullSyncNow() }
                .onError { Log.d(TAG, "Sync pull failed", it) }
                .subscribe(
                        {
                            Log.i(TAG, "Sync finished")
                            makeToast("Sync finished")
                            appComponent.eventBus.post(RealmSyncFinishedEvent())
                        },
                        { Log.w(TAG, "Sync failed") }
                )
    }

    fun makeToast(str: String) {
        handler.post {
            context.makeToast(str)
        }
    }

}