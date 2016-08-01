package ru.wutiarn.edustor.android.data.models.util.sync

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

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

    val getStatusString: String
        get() {
            return "Not implemented"
        }
}