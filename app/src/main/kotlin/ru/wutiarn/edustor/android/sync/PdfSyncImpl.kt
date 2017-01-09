package ru.wutiarn.edustor.android.sync

import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Page
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.events.PdfSyncProgressEvent
import ru.wutiarn.edustor.android.util.ProgressResponseBody
import ru.wutiarn.edustor.android.util.extension.getCacheFile
import ru.wutiarn.edustor.android.util.extension.getPdfUrl
import ru.wutiarn.edustor.android.util.extension.setUpSyncState
import rx.Observable
import rx.lang.kotlin.toObservable

class PdfSyncImpl(val context: Context,
                  val appComponent: AppComponent,
                  val notificationService: NotificationManager,
                  val notificationId: Int) {

    val handler = Handler(context.mainLooper)
    val TAG = "SyncAdapter.Pdf"

    val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cached_black_24dp)
                .setContentTitle("Edustor PDF Sync")
                .setProgress(0, 0, true)

    val httpClient = makeHttpClient()
    var updateListener: ((progress: Double, done: Boolean) -> Unit)? = null


    fun syncPdf() {
        notificationService.notify(notificationId,
                notificationBuilder
                        .setContentText("Checking current state")
                        .build()
        )

        val lessons = Realm.getDefaultInstance().where(Lesson::class.java)
                .findAll()
                .toObservable()
                .setUpSyncState(appComponent.pdfSyncManager, true)
                .toList()
                .toBlocking()
                .first()
                .sortedByDescending { it.realmDate }

        val syncable = lessons
                .filter { it.syncStatus!!.shouldBeSynced(appComponent.pdfSyncManager) }
                .filter { it.pages.filter(Page::isUploaded).count() > 0 }

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
                    if (now > nextReportInstant || done) {
                        nextReportInstant = now.plusSeconds(1)
                        val latestPercent = progress.toInt()
                        val lessonId = lesson.id

                        val progressNotification = notificationBuilder.setProgress(filesPercentSum, 100 * i + latestPercent, false)
                                .setContentText("[$i/$toSyncCount] Current file: $latestPercent%")
                                .build()
                        notificationService.notify(notificationId, progressNotification)

                        handler.post {
                            appComponent.eventBus.post(PdfSyncProgressEvent(lessonId, latestPercent, done))
                        }
                    }
                }
                downloadPdf(lesson)
            }
                    .toList()
                    .subscribe(
                            { },
                            {
                                val msg = "PDF Sync failed: ${it.javaClass.name}: ${it.message}"
                                Log.w(TAG, msg, it)
                                throw SyncException(msg)
                            }
                    )
        }
    }

    private fun downloadPdf(lesson: Lesson) {
        val pdfUrl = lesson.getPdfUrl(appComponent.constants.pdf_url)
        val cacheFile = lesson.getCacheFile(context)
        cacheFile.parentFile.mkdirs()

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
                lesson.forEach { it.syncStatus?.pageMD5?.clear() }
            }
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