package ru.wutiarn.edustor.android.data.models

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.bson.types.ObjectId
import org.threeten.bp.Instant

@RealmClass
open class Document() : RealmObject() {
    open var uuid: String? = null
    open var isUploaded: Boolean = false
    @Ignore open var timestamp: Instant = Instant.now()
    @Ignore open var uploadedTimestamp: Instant? = null
    @PrimaryKey open var id: String = ObjectId.get().toString()

    val shortUUID: String
        get() {
            val uuidEnd = uuid?.split("-")?.last()
            return uuidEnd?.let { "#${uuidEnd.substring(0, 4)}-${uuidEnd.substring(4, 8)}-${uuidEnd.substring(8, 12)}" } ?: "No uuid"
        }
}