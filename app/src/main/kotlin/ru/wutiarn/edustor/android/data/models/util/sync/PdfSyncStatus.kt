package ru.wutiarn.edustor.android.data.models.util.sync

import android.content.Context
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.extension.getCacheFile

@RealmClass
open class PdfSyncStatus() : RealmObject() {
    open var subjectId: String? = null
    open var realmDate: Long = 0
    open var markedForSync = false  // By user on LessonDetails
    open var documentsMD5 = RealmList<DocumentMD5>()

    constructor(subjectId: String, realmDate: Long) : this() {
        this.subjectId = subjectId
        this.realmDate = realmDate
    }

    fun getStatus(lesson: Lesson, context: Context): SyncStatus {
        val file = lesson.getCacheFile(context)

        if (!file.exists()) return SyncStatus.MISSING

        val actualMD5List = lesson.documents.filter { it.fileMD5 != null }.map { it.fileMD5 }
        if (actualMD5List != documentsMD5.toList()) return SyncStatus.OBSOLETE

        return SyncStatus.SYNCED
    }

    enum class SyncStatus(val status: Int) {
        SYNCED(0),
        MISSING(1),
        OBSOLETE(2)
    }
}