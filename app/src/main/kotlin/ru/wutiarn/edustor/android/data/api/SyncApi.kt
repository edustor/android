package ru.wutiarn.edustor.android.data.api

import retrofit2.http.GET
import ru.wutiarn.edustor.android.data.models.FullSyncData
import rx.Observable

interface SyncApi {
    @GET("sync/fetch")
    fun fetch(): Observable<FullSyncData>
}