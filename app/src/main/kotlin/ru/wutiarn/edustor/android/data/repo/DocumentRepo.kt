package ru.wutiarn.edustor.android.data.repo

import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.models.Document
import rx.Observable

interface DocumentRepo {
    fun activateQR(qr: String,
                   lessonId: String,
                   instant: Instant = Instant.now()
    ): Observable<Document>

    fun delete(documentId: String): Observable<Unit>
}