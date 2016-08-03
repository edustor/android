package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import retrofit2.adapter.rxjava.HttpException
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTaskResult
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


    override fun onPerformSync(account: Account?, extras: Bundle, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {

        val uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)
        val isManual = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL)


        val tasks = appComponent.syncManager.popAllTasks()

        val tasksCount = tasks.count()
        Log.i(TAG, "Sync $tasksCount changes. Full: ${!uploadOnly}")
        if (!uploadOnly) {
            makeSnack("Edustor is syncing ($tasksCount local changes)...", Snackbar.LENGTH_INDEFINITE)
        }

        var exceptionAlreadyLogged = false
        var pushResult = emptyList<SyncTaskResult>()

        appComponent.api.sync.push(tasks)
                .onError {
                    Log.w(TAG, "Sync push failed", it)
                    appComponent.syncManager.pushAllTasks(tasks, true)
                    exceptionAlreadyLogged = true
                }
                .map {
                    pushResult = it
                }
                .flatMap { if (!uploadOnly) appComponent.api.sync.fullSyncNow() else Observable.just(Unit) }
                .onError { if (!exceptionAlreadyLogged) Log.d(TAG, "Sync pull failed", it) }
                .subscribe(
                        {
                            val taskSucceeded = pushResult.count { it.success }
                            val msgStr = "Sync finished. Tasks succeeded: $taskSucceeded/${tasks.size}"
                            Log.i(TAG, msgStr)
                            makeSnack(msgStr)
                            appComponent.pdfSyncManager.requestSync(isManual)
                            handler.post {
                                appComponent.eventBus.post(RealmSyncFinishedEvent())
                            }
                        },
                        {
                            if (it is HttpException) {
                                when (it.code()) {
                                    401 -> appComponent.syncManager.syncEnabled = false
                                    403 -> appComponent.activeSession.logout()
                                    else -> syncResult.stats.numIoExceptions++
                                }
                            } else if (it is IOException) {
                                syncResult.stats.numIoExceptions++
                            }
                            val taskSucceeded = pushResult.count { it.success }
                            Log.w(TAG, "Sync failed")
                            makeSnack("Sync failed: ${it.javaClass.name}: ${it.message}. " +
                                    "Tasks succeeded: $taskSucceeded/${tasks.size}", 10000)
                        }
                )
    }

    private fun makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
        handler.post {
            appComponent.eventBus.makeSnack(str, length)
        }
    }
}