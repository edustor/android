package ru.wutiarn.edustor.android.data.local

import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Bundle
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.data.models.util.sync.SubjectSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {

    val constants: EdustorConstants

    init {
        constants = EdustorConstants(context)
    }

    var syncEnabled: Boolean
        get() = ContentResolver.getSyncAutomatically(constants.syncAccount, constants.pdfContentProviderAuthority)
        set(value) {
            ContentResolver.setSyncAutomatically(constants.syncAccount, constants.pdfContentProviderAuthority, value)
        }

    fun requestSync(manual: Boolean = false) {
        val bundle = Bundle()
        val syncRequest = SyncRequest.Builder()
                .setSyncAdapter(constants.syncAccount, constants.pdfContentProviderAuthority)
                .setExtras(bundle)
                .setManual(manual)
                .setExpedited(manual)
                .build()
        ContentResolver.requestSync(syncRequest)
    }

    fun getSyncStatus(lesson: Lesson, executeSynchronously: Boolean = false): Observable<PdfSyncStatus> {
        return Realm.getDefaultInstance().use { realm ->
            val query = realm.where(PdfSyncStatus::class.java)
                    .equalTo("subjectId", lesson.subject.id)
                    .equalTo("realmDate", lesson.realmDate)

            if (executeSynchronously) {
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
    }

    fun getSubjectSyncStatus(subjectId: String): SubjectSyncStatus {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(SubjectSyncStatus::class.java)
                    .equalTo("subjectId", subjectId)
                    .findFirst() ?: createSubjectSyncStatus(subjectId)
        }
    }

    private fun createSyncStatus(lesson: Lesson): PdfSyncStatus {
        var pdfSyncStatus = PdfSyncStatus(lesson.subject.id, lesson.realmDate)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                pdfSyncStatus = it.copyToRealm(pdfSyncStatus)
            }
        }

        return pdfSyncStatus
    }

    private fun createSubjectSyncStatus(subjectId: String): SubjectSyncStatus {
        var pdfSyncStatus = SubjectSyncStatus(subjectId)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                pdfSyncStatus = it.copyToRealm(pdfSyncStatus)
            }
        }

        return pdfSyncStatus
    }
}
