package ru.wutiarn.edustor.android.sync.stub

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.wutiarn.edustor.android.sync.PdfSyncAdapter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class PdfSyncService : Service() {

    var adapter: PdfSyncAdapter? = null
    var lock: ReentrantLock = ReentrantLock()

    override fun onCreate() {
        lock.withLock {
            if (adapter == null) {
                adapter = PdfSyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return adapter!!.syncAdapterBinder
    }

}