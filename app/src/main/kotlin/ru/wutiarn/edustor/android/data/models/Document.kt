package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId
import org.threeten.bp.Instant

open class Document() {
    lateinit var owner: User
    var uuid: String? = null
    var isUploaded: Boolean = false
    var timestamp: Instant = Instant.now()
    var uploadedTimestamp: Instant? = null
    var id: String = ObjectId.get().toString()

    val shortUUID: String
        get() {
            val uuidEnd = uuid?.split("-")?.last()
            return uuidEnd?.let { "#${uuidEnd.substring(0, 4)}-${uuidEnd.substring(4, 8)}-${uuidEnd.substring(8, 12)}" } ?: "No uuid"
        }
}