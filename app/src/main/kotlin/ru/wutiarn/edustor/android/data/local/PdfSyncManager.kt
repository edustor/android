package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.data.models.util.sync.TagSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {

    fun getSyncStatus(lesson: Lesson, executeSynchronously: Boolean = false): Observable<PdfSyncStatus> {
        return Realm.getDefaultInstance().use { realm ->
            val query = realm.where(PdfSyncStatus::class.java)
                    .equalTo("lessonId", lesson.id)

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

    fun getTagSyncStatus(tagId: String): TagSyncStatus {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(TagSyncStatus::class.java)
                    .equalTo("tagId", tagId)
                    .findFirst() ?: createTagSyncStatus(tagId)
        }
    }

    private fun createSyncStatus(lesson: Lesson): PdfSyncStatus {
        var pdfSyncStatus = PdfSyncStatus(lesson.id)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                pdfSyncStatus = it.copyToRealm(pdfSyncStatus)
            }
        }

        return pdfSyncStatus
    }

    private fun createTagSyncStatus(tagId: String): TagSyncStatus {
        var pdfSyncStatus = TagSyncStatus(tagId)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                pdfSyncStatus = it.copyToRealm(pdfSyncStatus)
            }
        }

        return pdfSyncStatus
    }
}
