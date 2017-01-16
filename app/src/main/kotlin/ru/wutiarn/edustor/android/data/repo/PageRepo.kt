package ru.wutiarn.edustor.android.data.repo

import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.models.Page
import rx.Observable

interface PageRepo {
    fun activateQR(qr: String,
                   lessonId: String,
                   instant: Instant = Instant.now()
    ): Observable<Page>

    fun delete(pageId: String): Observable<Unit>
}