package ru.wutiarn.edustor.android.data.local

import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Bundle
import ru.wutiarn.edustor.android.data.local.EdustorConstants
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyncManager(val prefs: EdustorPreferences,
                  val constants: EdustorConstants,
                  val context: Context) {

    companion object {
        private val tasksModificationLock = ReentrantLock()
    }

    var syncEnabled: Boolean
        get() = ContentResolver.getSyncAutomatically(constants.syncAccount, constants.contentProviderAuthority)
        set(value) {
            ContentResolver.setSyncAutomatically(constants.syncAccount, constants.contentProviderAuthority, value)
        }

    fun requestSync(manual: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, !manual)
        val syncRequest = SyncRequest.Builder()
                .setSyncAdapter(constants.syncAccount, constants.contentProviderAuthority)
                .setExtras(bundle)
                .setManual(manual)
                .setExpedited(manual)
                .build()
        ContentResolver.requestSync(syncRequest)
    }


    fun addTask(syncTask: SyncTask) {
        modifyTasksWithLock {
            it.add(syncTask)
        }
        requestSync()
    }

    fun popAllTasks(): List<SyncTask> {
        var tasks: List<SyncTask> = listOf()
        modifyTasksWithLock {
            tasks = it.toList()
            it.clear()
        }
        return tasks
    }

    fun pushAllTasks(tasks: List<SyncTask>, toBeginning: Boolean) {
        modifyTasksWithLock {
            if (toBeginning) it.addAll(0, tasks) else it.addAll(tasks)
        }
    }

    //    TODO: Consider executor usage
    fun modifyTasksWithLock(f: (MutableList<SyncTask>) -> Unit) {
        tasksModificationLock.withLock {
            val tasks = prefs.syncTasks.toMutableList()
            f(tasks)
            prefs.syncTasks = tasks
        }
    }
}