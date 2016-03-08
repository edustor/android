package ru.wutiarn.edustor.android.data.api

import okhttp3.ResponseBody
import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Document
import rx.Observable
import java.util.*

/**
 * Created by wutiarn on 02.03.16.
 */
interface DocumentsApi {
    @POST("documents/uuid/activate")
    @FormUrlEncoded
    fun activateUUID(@Field("uuid") uuid: String, @Field("lesson") lesson: String = "current", @Field("offset") offset: Int = TimeZone.getDefault().rawOffset / 3600000): Observable<Document>;

    @GET("documents/uuid/{uuid}")
    fun UUIDInfo(@Path("uuid") uuid: String): Observable<Document>

    @DELETE("documents/{document}")
    fun delete(@Path("document") documentId: String): Observable<ResponseBody>
}