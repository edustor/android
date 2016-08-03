package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.app.NotificationManager
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.events.PdfSyncProgressEvent
import ru.wutiarn.edustor.android.util.ProgressResponseBody
import ru.wutiarn.edustor.android.util.extension.*
import rx.Observable
import rx.lang.kotlin.toObservable

class PdfSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    val appComponent = context.initializeNewAppComponent()
    val handler = Handler(context.mainLooper)
    val notificationService = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val TAG = "PdfSyncAdapter"
    val NOTIFICATION_ID = 0

    val httpClient = makeHttpClient()
    var updateListener: ((progress: Double, done: Boolean) -> Unit)? = null

    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onPerformSync(account: Account?, extras: Bundle, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {

        notificationBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cached_black_24dp)
                .setContentTitle("Edustor PDF Sync")
                .setContentText("Preparing sync...")
                .setProgress(0, 0, true)

        notificationService.notify(NOTIFICATION_ID, notificationBuilder.build())

        val lessons = Realm.getDefaultInstance().where(Lesson::class.java)
                .findAll()
                .toObservable()
                .setUpSyncState(appComponent.pdfSyncManager, true)
                .toList()
                .toBlocking()
                .first()
                .sortedByDescending { it.realmDate }

        val syncable = lessons
                .filter { it.syncStatus!!.shouldBeSynced(it) }
                .filter { it.documents.filter { it.isUploaded }.count() > 0 }

        val otherLessons = lessons.minus(syncable)

        removePdfs(otherLessons)
        val toSync = syncable.filter { it.syncStatus!!.getStatus(it, context) != PdfSyncStatus.SyncStatus.SYNCED }
        val toSyncCount = toSync.size

        val filesPercentSum = toSyncCount * 100

        var nextReportInstant: Instant = Instant.now()

        if (toSyncCount > 0) {
            Observable.range(0, toSyncCount).map { i ->
                val lesson = toSync[i]

                updateListener = { progress, done ->
                    val now = Instant.now()
                    if (now > nextReportInstant) {
                        nextReportInstant = now.plusSeconds(1)
                        val latestPercent = progress.toInt()
                        val lessonId = lesson.id

                        val progressNotification = notificationBuilder.setProgress(filesPercentSum, 100 * i + latestPercent, false)
                                .setContentText("[$i/$toSyncCount] Current file: $latestPercent%")
                                .build()

                        notificationService.notify(NOTIFICATION_ID, progressNotification)

                        handler.post {
                            appComponent.eventBus.post(PdfSyncProgressEvent(lessonId, latestPercent, done))
                        }
                    }
                }
                downloadPdf(lesson)
            }
                    .toList()
                    .subscribe(
                            { notificationService.cancel(NOTIFICATION_ID) },
                            {
                                val msg = "PDF Sync failed: ${it.javaClass.name}: ${it.message}"

                                Log.w(TAG, msg, it)

                                val errorBuilder = NotificationCompat.Builder(appComponent.context)
                                        .setContentTitle("Edustor PDF Sync failed")
                                        .setContentText(msg)
                                        .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                                val errorNotification = NotificationCompat.BigTextStyle(errorBuilder)
                                        .bigText(msg)
                                        .build()
                                notificationService.notify(NOTIFICATION_ID, errorNotification)
                            }
                    )
        } else {
            notificationService.cancel(NOTIFICATION_ID)
        }
    }

    private fun downloadPdf(lesson: Lesson) {
        val pdfUrl = lesson.getPdfUrl(appComponent.constants.URL)
        val cacheFile = lesson.getCacheFile(context)

        val request = Request.Builder().url(pdfUrl).build()
        val response = httpClient.newCall(request).execute()

        cacheFile.outputStream().use { outStream ->
            response.body().byteStream().use { inStream ->
                inStream.copyTo(outStream)
            }
        }

        lesson.syncStatus!!.copyMD5List(lesson)
    }

    private fun removePdfs(lesson: List<Lesson>) {
        lesson.forEach { it.getCacheFile(context).delete() }
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                lesson.forEach { it.syncStatus?.documentsMD5?.clear() }
            }
        }
    }

    private fun makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
        handler.post {
            appComponent.eventBus.makeSnack(str, length)
        }
    }

    private fun makeHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor {
            val resp = it.proceed(it.request())
            return@addInterceptor resp.newBuilder().body(ProgressResponseBody(resp.body(), { progress, done ->
                updateListener?.let { it(progress, done) }
            })).build()
        }.build()
    }
}