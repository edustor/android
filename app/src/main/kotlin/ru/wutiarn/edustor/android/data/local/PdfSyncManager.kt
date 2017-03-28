package ru.wutiarn.edustor.android.data.local

import android.content.Context
import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.data.models.util.sync.TagSyncStatus
import rx.Observable

class PdfSyncManager(val context: Context) {

    fun getSyncStatus(lessonId: String): PdfSyncStatus {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(PdfSyncStatus::class.java)
                    .equalTo("lessonId", lessonId)
                    .findFirst() ?: createSyncStatus(lessonId)
        }
    }

    fun getTagSyncStatus(tagId: String): TagSyncStatus {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(TagSyncStatus::class.java)
                    .equalTo("tagId", tagId)
                    .findFirst() ?: createTagSyncStatus(tagId)
        }
    }

    private fun createSyncStatus(lessonId: String): PdfSyncStatus {
        var pdfSyncStatus = PdfSyncStatus(lessonId)
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
