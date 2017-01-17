package ru.wutiarn.edustor.android.data.models.util.sync

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class TagSyncStatus() : RealmObject() {
    open lateinit var tagId: String
    open var markedForSync: Boolean = false

    @Suppress("LeakingThis")
    constructor(tagId: String) : this() {
        this.tagId = tagId
    }
}