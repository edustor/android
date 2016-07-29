package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import retrofit2.adapter.rxjava.HttpException
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.util.extension.fullSyncNow
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent
import ru.wutiarn.edustor.android.util.extension.makeSnack
import rx.Observable
import rx.lang.kotlin.onError
import java.io.IOException

class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {

    val appComponent = context.initializeNewAppComponent()
    val handler = Handler(context.mainLooper)
    val TAG = "SyncAdapter"


    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {
        val tasks = appComponent.syncManager.popAllTasks()

        val startStr = "Edustor is syncing... (${tasks.count()} local changes)"
        Log.i(TAG, startStr)
        makeSnack(startStr, Snackbar.LENGTH_INDEFINITE)

        var exceptionAlreadyLogged = false

        appComponent.api.sync.push(tasks)
                .onError {
                    Log.w(TAG, "Sync push failed", it)
                    appComponent.syncManager.pushAllTasks(tasks, true)
                    exceptionAlreadyLogged = true
                }
                .flatMap { appComponent.api.sync.fullSyncNow() }
                .onError { if (!exceptionAlreadyLogged) Log.d(TAG, "Sync pull failed", it) }
                .subscribe(
                        {
                            Log.i(TAG, "Sync finished")
                            makeSnack("Sync finished")
                            appComponent.eventBus.post(RealmSyncFinishedEvent())
                        },
                        {
                            if (it is HttpException) {
                                if (it.code() == 401) {
                                    appComponent.syncManager.syncEnabled = false
                                } else {
                                    syncResult.stats.numIoExceptions++
                                }
                            } else if (it is IOException) {
                                syncResult.stats.numIoExceptions++
                            }
                            Log.w(TAG, "Sync failed")
                            makeSnack("Sync failed: ${it.javaClass.name}: ${it.message}", 10000)
                        }
                )
    }

    private fun makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
        handler.post {
            appComponent.eventBus.makeSnack(str, length)
        }
    }
}