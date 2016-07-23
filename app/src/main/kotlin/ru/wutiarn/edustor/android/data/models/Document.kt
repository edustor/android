package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId
import org.threeten.bp.Instant

class Document() {
    lateinit var owner: User
    lateinit var uuid: String
    var isUploaded: Boolean = false
    var timestamp: Instant = Instant.now()
    var uploadedTimestamp: Instant? = null
    var id: String = ObjectId.get().toString()

    val shortUUID: String
        get() {
            val uuidEnd = uuid.split("-").last()
            return "#${uuidEnd.substring(0, 4)}-${uuidEnd.substring(4, 8)}-${uuidEnd.substring(8, 12)}"
        }
}