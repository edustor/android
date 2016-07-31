package ru.wutiarn.edustor.android.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.ProgressResponseBody
import ru.wutiarn.edustor.android.util.extension.*
import rx.lang.kotlin.toObservable

class PdfSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    val appComponent = context.initializeNewAppComponent()
    val handler = Handler(context.mainLooper)
    val TAG = "PdfSyncAdapter"

    val httpClient = makeHttpClient()
    var updateListener: ((progress: Double, done: Boolean) -> Unit)? = null

    override fun onPerformSync(account: Account?, extras: Bundle, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {
        val syncDocumentsSince = LocalDate.now().minusDays(3).toEpochDay()
        Realm.getDefaultInstance().where(Lesson::class.java)
                .findAll()
                .toObservable()
                .setUpSyncState(appComponent.pdfSyncManager, true)
                .filter { it.syncStatus!!.markedForSync || it.realmDate >= syncDocumentsSince }
                .filter { it.documents.filter { it.isUploaded }.count() > 0 }
                .toList()
                .subscribe {
                    Log.i("PdfSyncAdapter", "Found ${it.count()} lessons")
//                    it.firstOrNull()?.let { downloadPdf(it) }
                }
    }

    private fun downloadPdf(lesson: Lesson) {
        val pdfUrl = lesson.getPdfUrl(appComponent.constants.URL)
        val cacheFile = lesson.getCacheFile(context)

        updateListener = { progress, done ->
            Log.d(TAG, "Downloading ${lesson.id}. Progress: $progress. Done: $done")
        }

        val request = Request.Builder().url(pdfUrl).build()
        val response = httpClient.newCall(request).execute()

        val out = cacheFile.outputStream().use {
            response.body().byteStream().copyTo(it)
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