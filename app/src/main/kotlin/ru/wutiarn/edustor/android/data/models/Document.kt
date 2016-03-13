package ru.wutiarn.edustor.android.data.models

import org.threeten.bp.Instant

/**
 * Created by wutiarn on 04.03.16.
 */
data class Document(
        var owner: User? = null,
        var uuid: String? = null,
        var isUploaded: Boolean = false,
        var timestamp: Instant? = null,
        var uploadedTimestamp: Instant? = null,
        var id: String? = null
) {
    val shortUUID: String?
        get() = uuid?.split("-")?.last()
}