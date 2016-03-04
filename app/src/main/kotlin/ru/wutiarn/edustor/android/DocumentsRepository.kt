package ru.wutiarn.edustor.android

import android.util.Log
import retrofit2.Retrofit
import ru.wutiarn.edustor.android.data.repository.DocumentsApi
import rx.Observable
import rx.schedulers.Schedulers

class DocumentsRepository {

    constructor(retrofit: Retrofit) {
        this.retrofit = retrofit
    }

    lateinit var retrofit: Retrofit

    fun documentUUIDInfo(uuid: String) {
        val docRepo = retrofit.create(DocumentsApi::class.java)
        docRepo.UUIDInfo(uuid)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext { println("NORMAL HANDLER"); Observable.empty() }
                .subscribe { Log.i("UUID RESULT", "Response: ${it.string()}") }
    }
}