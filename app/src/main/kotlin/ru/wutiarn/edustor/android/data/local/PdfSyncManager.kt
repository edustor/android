package ru.wutiarn.edustor.android.data.local

import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Bundle
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {

    val constants: EdustorConstants

    init {
        constants = EdustorConstants(context)
    }

    fun requestSync(manual: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, !manual)
        val syncRequest = SyncRequest.Builder()
                .setSyncAdapter(constants.syncAccount, constants.pdfContentProviderAuthority)
                .setExtras(bundle)
                .setManual(manual)
                .setExpedited(manual)
                .build()
        ContentResolver.requestSync(syncRequest)
    }

    fun getSyncStatus(lesson: Lesson, sync: Boolean = false): Observable<PdfSyncStatus> {
        val realm = Realm.getDefaultInstance()
        val query = realm.where(PdfSyncStatus::class.java)
                .equalTo("subjectId", lesson.subject.id)
                .equalTo("realmDate", lesson.realmDate)

        if (sync) {
            val syncStatus = query.findFirst() ?: createSyncStatus(lesson)
            return Observable.just(syncStatus)
        } else {
            return query.findFirstAsync()
                    .asObservable<PdfSyncStatus>()
                    .filter { it.isLoaded }
                    .flatMap {
                        if (it != null && it.isValid) return@flatMap Observable.just(it)
                        return@flatMap Observable.just(createSyncStatus(lesson))
                    }
        }
    }

    private fun createSyncStatus(lesson: Lesson): PdfSyncStatus {
        val pdfSyncStatus = PdfSyncStatus(lesson.subject.id, lesson.realmDate)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                it.copyToRealm(pdfSyncStatus)
            }
        }

        return pdfSyncStatus
    }
}
