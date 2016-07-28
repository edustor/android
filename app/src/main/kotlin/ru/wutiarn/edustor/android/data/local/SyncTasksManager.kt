package ru.wutiarn.edustor.android.data.local

import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyncTasksManager(val prefs: EdustorPreferences) {
    companion object {
        private val lock = ReentrantLock()
    }

    fun addTask(syncTask: SyncTask) {
        modifyTasksWithLock {
            it.add(syncTask)
        }
    }

    //    TODO: Consider executor usage
    fun modifyTasksWithLock(f: (MutableList<SyncTask>) -> Unit) {
        lock.withLock {
            val tasks = prefs.syncTasks.toMutableList()
            f(tasks)
            prefs.syncTasks = tasks
        }
    }
}