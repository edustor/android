package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {
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
