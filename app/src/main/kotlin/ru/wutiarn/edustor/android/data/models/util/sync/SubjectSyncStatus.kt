package ru.wutiarn.edustor.android.data.models.util.sync

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class SubjectSyncStatus() : RealmObject() {
    open lateinit var subjectId: String
    open var markedForSync: Boolean = false

    constructor(subjectId: String) : this() {
        this.subjectId = subjectId
    }
}