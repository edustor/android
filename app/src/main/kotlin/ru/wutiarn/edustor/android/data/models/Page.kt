package ru.wutiarn.edustor.android.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.threeten.bp.Instant
import java.util.*

@RealmClass
open class Page() : RealmObject() {
    open var qr: String? = null
    open var isUploaded: Boolean = false
    open var fileMD5: String? = null
    open var contentType: String? = null
    @Ignore open var timestamp: Instant = Instant.now()
        get() = Instant.ofEpochSecond(realmTimestamp)
        set(value) {
            field = value
            realmTimestamp = value.epochSecond
        }
    @Ignore open var uploadedTimestamp: Instant? = null
        get() = realmUploadedTimestamp?.let { Instant.ofEpochSecond(it) }
        set(value) {
            field = value
            realmUploadedTimestamp = value?.epochSecond
        }
    @PrimaryKey open var id: String = UUID.randomUUID().toString()

    @JsonIgnore var realmTimestamp: Long = 0
    @JsonIgnore var realmUploadedTimestamp: Long? = null

    @JsonIgnore var index: Int = 0

    val shortQR: String
        get() {
            val uuidEnd = qr?.split("-")?.last()
            return uuidEnd?.let { "#${uuidEnd.substring(0, 4)}-${uuidEnd.substring(4, 8)}-${uuidEnd.substring(8, 12)}" } ?: "No qr"
        }

    @Suppress("LeakingThis")
    constructor(qr: String, timestamp: Instant, index: Int) : this() {
        this.qr = qr
        this.timestamp = timestamp
        this.index = index
    }

    @Suppress("LeakingThis")
    constructor(dto: PageDTO) : this() {
        this.id = dto.id
        this.index = dto.index
        this.timestamp = dto.timestamp
        this.isUploaded = dto.uploaded
        this.uploadedTimestamp = dto.uploadedTimestamp
        this.qr = dto.qr
        this.contentType = dto.contentType
    }

    data class PageDTO(
            val id: String,
            val index: Int,
            val timestamp: Instant,
            val uploaded: Boolean,
            val uploadedTimestamp: Instant?,
            val qr: String?,
            val contentType: String?,
            val removed: Boolean
    )
}