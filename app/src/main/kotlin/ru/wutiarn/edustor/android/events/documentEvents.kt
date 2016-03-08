package ru.wutiarn.edustor.android.events

import ru.wutiarn.edustor.android.data.models.Document

/**
 * Created by wutiarn on 08.03.16.
 */
data class DocumentRemovedEvent(val document: Document)

data class DocumentMovedEvent(val document: Document, val after: Document)

/**
 * Broadcasted when there is open bottom panel with lesson fragment
 */
data class NewDocumentQrCodeScanned(val string: String)