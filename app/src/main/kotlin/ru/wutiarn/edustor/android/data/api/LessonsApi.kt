package ru.wutiarn.edustor.android.data.api

import okhttp3.ResponseBody
import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable
import java.util.*

/**
 * Created by wutiarn on 02.03.16.
 */
interface LessonsApi {

    @GET("lessons/uuid/{uuid}")
    fun byUUID(@Path("uuid") uuid: String): Observable<Lesson>

    @GET("lessons/current")
    fun current(@Query("offset") offset: Int = TimeZone.getDefault().rawOffset / 3600000): Observable<Lesson>

    @GET("lessons/today")
    fun today(@Query("offset") offset: Int = TimeZone.getDefault().rawOffset / 3600000): Observable<List<Lesson>>

    @GET("lessons/{id}")
    fun byId(@Query("id") id: String): Observable<Lesson>

    @FormUrlEncoded
    @POST("lessons/{lesson}/documents/reorder")
    fun reorderDocuments(@Path("lesson") lesson: String, @Field("document") document: String, @Field("after") after: String?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("lessons/{lesson}/topic")
    fun setTopic(@Path("lesson") lesson: String, @Field("topic") topic: String): Observable<ResponseBody>
}