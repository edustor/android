package ru.wutiarn.edustor.android.events

import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson

/**
 * Created by wutiarn on 08.03.16.
 */
interface DocumentChangedEvent

data class DocumentRemovedEvent(val document: Document) : DocumentChangedEvent

data class DocumentMovedEvent(val lesson: Lesson,
                              val document: Document,
                              val after: Document?,
                              val fromPos: Int,
                              val toPos: Int
) : DocumentChangedEvent

/**
 * Broadcasted when there is open bottom panel with lesson fragment
 */
data class NewDocumentQrCodeScanned(val string: String)