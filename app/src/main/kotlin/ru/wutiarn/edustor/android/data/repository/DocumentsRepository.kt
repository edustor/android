package ru.wutiarn.edustor.android.data.repository

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * Created by wutiarn on 02.03.16.
 */
interface DocumentsRepository {
    @POST("documents/activate_uuid")
    @FormUrlEncoded
    fun activateUUID(@Field("uuid") uuid: String, @Field("offset") offset: Int): Observable<ResponseBody>;
}