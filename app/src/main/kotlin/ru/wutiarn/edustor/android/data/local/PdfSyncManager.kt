package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {
    fun getSyncStatus(lesson: Lesson): Observable<PdfSyncStatus> {
        val realm = Realm.getDefaultInstance()
        return realm.where(PdfSyncStatus::class.java)
                .equalTo("subjectId", lesson.subject.id)
                .equalTo("realmDate", lesson.realmDate)
                .findFirstAsync()
                .asObservable<PdfSyncStatus>()
                .filter { it.isLoaded }
                .flatMap {
                    if (it.isValid) return@flatMap Observable.just(it)

                    val pdfSyncStatus = PdfSyncStatus(lesson.subject.id, lesson.realmDate)
                    realm.executeTransaction {
                        it.copyToRealm(pdfSyncStatus)
                    }

                    return@flatMap Observable.just(pdfSyncStatus)
                }
    }
}
