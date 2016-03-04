package ru.wutiarn.edustor.android.data.repository

import android.util.Log
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

class DocumentsRepository @Inject @AppScope constructor(val documentsApi: DocumentsApi) {
    fun documentUUIDInfo(uuid: String) {
        documentsApi.UUIDInfo(uuid)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext { it.printStackTrace(); Observable.empty() }
                .subscribe { Log.i("UUID RESULT", "Response: ${it.toString()}") }
    }
}