package ru.wutiarn.edustor.android.data.models.util.sync

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import ru.wutiarn.edustor.android.data.models.Lesson

@RealmClass
open class PdfSyncStatus() : RealmObject() {
    open var lesson: Lesson? = null
    open var markedForSync = false  // By user on LessonDetails
    open var synced = false
    open var fileLocation: String? = null

    constructor(lesson: Lesson) : this() {
        this.lesson = lesson
    }
}