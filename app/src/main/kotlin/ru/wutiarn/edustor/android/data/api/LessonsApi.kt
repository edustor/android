package ru.wutiarn.edustor.android.data.api

import okhttp3.ResponseBody
import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable
import java.util.*

interface LessonsApi {

    @GET("lessons/uuid/{uuid}")
    fun byUUID(@Path("uuid") uuid: String): Observable<Lesson>

    @GET("lessons/date")
    fun byDate(@Query("subject") subject: String, @Query("date") date: String): Observable<Lesson>

    @GET("lessons/{lesson}")
    fun byId(@Path("lesson") id: String): Observable<Lesson>

    @FormUrlEncoded
    @POST("lessons/{lesson}/documents/reorder")
    fun reorderDocuments(@Path("lesson") lesson: String, @Field("document") document: String, @Field("after") after: String?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("lessons/{lesson}/topic")
    fun setTopic(@Path("lesson") lesson: String, @Field("topic") topic: String): Observable<ResponseBody>

    @GET("subjects/{subject_id}/lessons")
    fun bySubjectId(@Path("subject_id") subject_id: String): Observable<List<Lesson>>
}