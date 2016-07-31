package ru.wutiarn.edustor.android.util.extension

import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

fun Observable<Lesson>.setUpSyncState(pdfSyncManager: PdfSyncManager): Observable<Lesson> {
    var lesson: Lesson? = null
    return this.flatMap { lesson = it; pdfSyncManager.getSyncStatus(it) }
            .map {
                lesson!!.syncStatus = it
                lesson
            }
}