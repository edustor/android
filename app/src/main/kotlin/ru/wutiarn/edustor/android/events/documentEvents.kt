package ru.wutiarn.edustor.android.events

import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson

/**
 * Broadcasted when there is open bottom panel with lesson fragment
 */
data class NewDocumentQrCodeScanned(val string: String)

interface DocumentChangedEvent

data class DocumentRemovedEvent(val document: Document, val position: Int) : DocumentChangedEvent

data class DocumentMovedEvent(val lesson: Lesson,
                              val document: Document,
                              val after: Document?,
                              val fromPos: Int,
                              val toPos: Int
) : DocumentChangedEvent

data class DocumentAddedEvent(val lesson: Lesson, val document: Document, val insertedPosition: Int = -1) : DocumentChangedEvent