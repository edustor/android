package ru.wutiarn.edustor.android.util.extension

import android.content.Context
import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable
import java.io.File

fun Observable<Lesson>.setUpSyncState(pdfSyncManager: PdfSyncManager, sync: Boolean = false): Observable<Lesson> {
    var lesson: Lesson? = null
    return this.flatMap { lesson = it; pdfSyncManager.getSyncStatus(it, sync) }
            .map {
                lesson!!.syncStatus = it
                lesson
            }
}

fun Lesson.getPdfUrl(edustorUrl: String): String {
    return "${edustorUrl}pdf/${this.id}"
}

fun Lesson.getCacheFile(context: Context): File {
    val file = File(context.getExternalFilesDir(null), "${this.id}.pdf")
    return file
}