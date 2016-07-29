package ru.wutiarn.edustor.android.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyncService : Service() {

    var adapter: SyncAdapter? = null
    var lock: ReentrantLock = ReentrantLock()

    override fun onCreate() {
        lock.withLock {
            if (adapter == null) {
                adapter = SyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return adapter!!.syncAdapterBinder
    }

}