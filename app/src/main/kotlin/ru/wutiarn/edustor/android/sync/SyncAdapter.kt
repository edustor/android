package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.util.Log
import retrofit2.adapter.rxjava.HttpException
import ru.wutiarn.edustor.android.R
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
    val notificationService = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val TAG = "SyncAdapter"
    val NOTIFICATION_ID = 0

    override fun onPerformSync(account: Account?, extras: Bundle, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {

        val uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)
        val isManual = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL)

        val notification = NotificationCompat.Builder(appComponent.context)
                .setSmallIcon(R.drawable.ic_cached_black_24dp)
                .setContentTitle("Edustor Sync")
                .setContentText("Edustor sync is in progress")
                .setProgress(0, 0, true).build()
        notificationService.notify(NOTIFICATION_ID, notification)

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

                            notificationService.cancel(NOTIFICATION_ID)
                        },
                        {
                            if (it is HttpException) {
                                when (it.code()) {
                                    403 -> appComponent.activeSession.logout()
                                    else -> syncResult.stats.numIoExceptions++
                                }
                            } else if (it is IOException) {
                                syncResult.stats.numIoExceptions++
                            }
                            val taskSucceeded = pushResult.count { it.success }
                            Log.w(TAG, "Sync failed")
                            val msg = "Sync failed: ${it.javaClass.name}: ${it.message}. " +
                                    "Tasks succeeded: $taskSucceeded/${tasks.size}"
                            makeSnack(msg, 10000)

                            val errorBuilder = NotificationCompat.Builder(appComponent.context)
                                    .setContentTitle("Edustor Sync failed")
                                    .setContentText(msg)
                                    .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                            val errorNotification = NotificationCompat.BigTextStyle(errorBuilder)
                                    .bigText(msg)
                                    .build()
                            notificationService.notify(NOTIFICATION_ID, errorNotification)
                        }
                )
    }

    private fun makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
        handler.post {
            appComponent.eventBus.makeSnack(str, length)
        }
    }
}