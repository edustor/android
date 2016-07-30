package ru.wutiarn.edustor.android.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.wutiarn.edustor.android.data.models.FullSyncData
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTaskResult
import rx.Observable

interface SyncApi {
    @GET("sync/fetch")
    fun fetch(): Observable<FullSyncData>

    @POST("sync/push")
    fun push(@Body body: List<SyncTask>): Observable<List<SyncTaskResult>>
}