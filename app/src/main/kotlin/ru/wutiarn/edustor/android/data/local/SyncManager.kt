package ru.wutiarn.edustor.android.data.local

import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyncManager(val prefs: EdustorPreferences, val syncApi: SyncApi) {
    companion object {
        private val lock = ReentrantLock()
    }

    fun addTask(syncTask: SyncTask) {
        modifyTasksWithLock {
            it.add(syncTask)
        }
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
        lock.withLock {
            val tasks = prefs.syncTasks.toMutableList()
            f(tasks)
            prefs.syncTasks = tasks
        }
    }
}