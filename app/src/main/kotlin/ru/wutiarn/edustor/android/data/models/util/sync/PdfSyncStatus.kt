package ru.wutiarn.edustor.android.data.models.util.sync

import android.content.Context
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.RealmClass
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.extension.getCacheFile

@RealmClass
open class PdfSyncStatus() : RealmObject() {

//    TODO: Use lesson id

    @Ignore private val CACHE_DAYS = 7

    open lateinit var lessonId: String
    open var markedForSync = false

    open var syncedUntil: Long? = null
        get() = if (markedForSync) null else field

    open var pageMD5 = RealmList<PageMD5>()


    @Suppress("LeakingThis")
    constructor(lessonId: String) : this() {
        this.lessonId = lessonId
    }

    fun setShouldBeSynced(value: Boolean) {
        syncedUntil = if (value) LocalDate.now().toEpochDay() + CACHE_DAYS else null
    }

    fun getStatus(lesson: Lesson, context: Context): SyncStatus {
        val file = lesson.getCacheFile(context)

        if (!file.exists()) return SyncStatus.MISSING

        val actualMD5List = getMD5List(lesson)
        if (actualMD5List != pageMD5.map { it.md5 }) return SyncStatus.OBSOLETE

        return SyncStatus.SYNCED
    }

    fun copyMD5List(lesson: Lesson) {
        val list = getMD5List(lesson)
                .map(::PageMD5)
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                pageMD5.clear()
                pageMD5.addAll(list)
            }
        }
    }

    private fun getMD5List(lesson: Lesson): List<String> {
        return lesson.pages.filter { it.fileMD5 != null }.map { it.fileMD5!! }
    }

    enum class SyncStatus {
        SYNCED,
        MISSING,
        OBSOLETE
    }
}