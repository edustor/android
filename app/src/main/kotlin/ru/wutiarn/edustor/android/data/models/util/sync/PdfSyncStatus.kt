package ru.wutiarn.edustor.android.data.models.util.sync

import android.content.Context
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.extension.getCacheFile

@RealmClass
open class PdfSyncStatus() : RealmObject() {
    open var subjectId: String? = null
    open var realmDate: Long = 0
    open var markedForSync = false  // By user on LessonDetails
        set(value) {
            field = value
            realmValidUntil = null
        }
    open var documentsMD5 = RealmList<DocumentMD5>()
    open var realmValidUntil: Long? = null
        get() = if (markedForSync) null else field
        set(value) {
            if (markedForSync) field = null
            else field = value
        }

    constructor(subjectId: String, realmDate: Long) : this() {
        this.subjectId = subjectId
        this.realmDate = realmDate
    }

    var shouldBeSynced: Boolean
        get() = markedForSync || (realmValidUntil != null && realmValidUntil!! >= LocalDate.now().toEpochDay())
        set(value) {
            realmValidUntil = if (value) LocalDate.now().toEpochDay() + 7 else null
        }

    fun getStatus(lesson: Lesson, context: Context): SyncStatus {
        val file = lesson.getCacheFile(context)

        if (!file.exists()) return SyncStatus.MISSING

        val actualMD5List = getMD5List(lesson)
        if (actualMD5List != documentsMD5.map { it.md5 }) return SyncStatus.OBSOLETE

        return SyncStatus.SYNCED
    }

    fun copyMD5List(lesson: Lesson) {
        val list = getMD5List(lesson)
                .map { DocumentMD5(it) }
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                documentsMD5.clear()
                documentsMD5.addAll(list)
            }
        }
    }

    private fun getMD5List(lesson: Lesson): List<String> {
        return lesson.documents.filter { it.fileMD5 != null }.map { it.fileMD5!! }
    }

    enum class SyncStatus(val status: Int) {
        SYNCED(0),
        MISSING(1),
        OBSOLETE(2)
    }
}