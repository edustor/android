package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import retrofit2.adapter.rxjava.HttpException
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent
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

        val metaSyncImpl = MetaSyncImpl(context, appComponent, notificationService, NOTIFICATION_ID)
        val pdfSyncImpl = PdfSyncImpl(context, appComponent, notificationService, NOTIFICATION_ID)

        try {
            metaSyncImpl.syncMeta(uploadOnly)
            if (!uploadOnly) {
                pdfSyncImpl.syncPdf()
            }
            notificationService.cancel(NOTIFICATION_ID)
        } catch (e: SyncException) {
            handleSyncException(e, syncResult)
            handler.post {
                appComponent.eventBus.post(EdustorMetaSyncFinished(false, e))
            }
        }
    }

    private fun handleSyncException(ex: SyncException, syncResult: SyncResult) {
        val cause = ex.cause

        if (cause is HttpException || cause is IOException) {
            syncResult.stats.numIoExceptions++ // 401 is already handled in retrofit interceptor
        }

        val msg = "Sync failed: ${ex.message}"
        Log.w(TAG, msg)

        val errorBuilder = NotificationCompat.Builder(appComponent.context)
                .setContentTitle("Edustor Sync failed")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
        val errorNotification = NotificationCompat.BigTextStyle(errorBuilder)
                .bigText(msg)
                .build()
        notificationService.notify(NOTIFICATION_ID, errorNotification)
    }
}