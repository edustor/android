package ru.wutiarn.edustor.android.util.extension

import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

fun Observable<Lesson>.setUpSyncState(pdfSyncManager: PdfSyncManager): Observable<Lesson> {
    return this.flatMap { pdfSyncManager.getSyncStatus(it) }
            .map { it.lesson!!.syncStatus = it; it.lesson }
}