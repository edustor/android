package ru.wutiarn.edustor.android.data.repository

import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import ru.wutiarn.edustor.android.data.models.Document
import rx.Observable
import javax.inject.Inject

class DocumentsRepository @Inject @AppScope constructor(val documentsApi: DocumentsApi) {
    fun documentUUIDInfo(uuid: String): Observable<Document> {
        return documentsApi.UUIDInfo(uuid)
        //                .observeOn(Schedulers.io())
        //                .subscribeOn(Schedulers.io())
    }
}