package ru.wutiarn.edustor.android.sync

import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.util.extension.fullSyncNow

class MetaSyncImpl(val context: Context,
                   val appComponent: AppComponent,
                   val notificationService: NotificationManager,
                   val notificationId: Int) {
    val TAG = "SyncAdapter.Meta"
    val handler = Handler(context.mainLooper)

    val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cached_black_24dp)
                .setContentTitle("Edustor Sync")
                .setProgress(0, 0, true)

    fun syncMeta(uploadOnly: Boolean) {
        val tasks = appComponent.syncManager.popAllTasks()
        val tasksCount = tasks.count()
        Log.i(TAG, "Sync $tasksCount changes. Full: ${!uploadOnly}")

        notificationService.notify(notificationId,
                notificationBuilder
                        .setContentText("Meta: Pushing $tasksCount changes")
                        .build()
        )

        appComponent.api.sync.push(tasks)
                .toBlocking()
                .subscribe({ pushResult ->
                    val tasksSucceeded = pushResult.count { it.success }
                    val msgStr = "Task push succeeded: $tasksSucceeded/${tasks.size}"
                    Log.i(TAG, msgStr)

                    if (!uploadOnly) {

                        notificationService.notify(notificationId,
                                notificationBuilder
                                        .setContentText("Meta: Fetching backend data")
                                        .build()
                        )

                        appComponent.api.sync.fullSyncNow()
                                .toBlocking()
                                .subscribe({
                                    handler.post {
                                        appComponent.eventBus.post(EdustorMetaSyncFinished(true))
                                    }
                                }, {
                                    throw SyncException("Fetch failed: $it")
                                })
                    }
                }, {
                    throw SyncException("Push failed: $it")
                })
    }
}