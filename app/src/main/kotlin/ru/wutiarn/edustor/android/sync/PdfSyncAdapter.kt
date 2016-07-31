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
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.util.extension.setUpSyncState
import rx.lang.kotlin.toObservable

class PdfSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    val appComponent = context.initializeNewAppComponent()
    val handler = Handler(context.mainLooper)
    val TAG = "PdfSyncAdapter"


    override fun onPerformSync(account: Account?, extras: Bundle, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult) {
        val syncDocumentsSince = LocalDate.now().minusDays(3).toEpochDay()
        Realm.getDefaultInstance().where(Lesson::class.java)
                .findAll()
                .toObservable()
                .setUpSyncState(appComponent.pdfSyncManager, true)
                .filter { it.syncStatus!!.markedForSync || it.realmDate >= syncDocumentsSince }
                .toList()
                .subscribe {
                    Log.i("PdfSyncAdapter", "Found ${it.count()} lessons")
                }
    }

    private fun makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
        handler.post {
            appComponent.eventBus.makeSnack(str, length)
        }
    }
}