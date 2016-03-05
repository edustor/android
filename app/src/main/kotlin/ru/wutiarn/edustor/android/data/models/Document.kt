package ru.wutiarn.edustor.android.data.models

import org.threeten.bp.Instant

/**
 * Created by wutiarn on 04.03.16.
 */
data class Document(
        var uuid: String? = null,
        var isUploaded: Boolean = false,
        var timestamp: Instant = Instant.now(),
        var id: String? = null
)