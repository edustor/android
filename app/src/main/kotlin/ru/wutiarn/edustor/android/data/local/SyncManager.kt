package ru.wutiarn.edustor.android.data.local

import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Bundle
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyncManager(val context: Context) {

    val prefs: EdustorPreferences
    val constants: EdustorConstants

    companion object {
        private val tasksModificationLock = ReentrantLock()
    }

    init {
        prefs = EdustorPreferences(context)
        constants = EdustorConstants(context)
    }

    var syncEnabled: Boolean
        get() = ContentResolver.getSyncAutomatically(constants.syncAccount, constants.syncContentProviderAuthority)
        set(value) {
            ContentResolver.setSyncAutomatically(constants.syncAccount, constants.syncContentProviderAuthority, value)
        }

    fun requestSync(manual: Boolean, uploadOnly: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, uploadOnly)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, manual)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, manual)
        val syncRequest = SyncRequest.Builder()
                .setSyncAdapter(constants.syncAccount, constants.syncContentProviderAuthority)
                .setExtras(bundle)
                .build()
        ContentResolver.requestSync(syncRequest)
    }


    fun addTask(syncTask: SyncTask) {
        modifyTasksWithLock {
            it.add(syncTask)
        }
        requestSync(true, true)
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