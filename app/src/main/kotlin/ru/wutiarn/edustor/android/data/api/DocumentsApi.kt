package ru.wutiarn.edustor.android.data.api

import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Document
import rx.Observable

/**
 * Created by wutiarn on 02.03.16.
 */
interface DocumentsApi {
    @POST("documents/uuid/activate")
    @FormUrlEncoded
    fun activateUUID(@Field("uuid") uuid: String, @Field("offset") offset: Int): Observable<Document>;

    @GET("documents/uuid/{uuid}")
    fun UUIDInfo(@Path("uuid") uuid: String): Observable<Document>
}