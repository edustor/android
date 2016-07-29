package ru.wutiarn.edustor.android.data.repo

import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

interface LessonsRepo {

    @GET("lessons/uuid/{uuid}")
    fun byUUID(@Path("uuid") uuid: String): Observable<Lesson>

    @GET("lessons/date")
    fun byDate(@Query("subject") subject: String, @Query("date") epochDay: Long): Observable<Lesson>

    @GET("lessons/{lesson}")
    fun byId(@Path("lesson") id: String): Observable<Lesson>

    @FormUrlEncoded
    @POST("lessons/{lesson}/documents/reorder")
    fun reorderDocuments(@Path("lesson") lesson: String, @Field("document") documentId: String, @Field("after") afterDocumentId: String?): Observable<Unit>

    @FormUrlEncoded
    @POST("lessons/{lesson}/topic")
    fun setTopic(@Path("lesson") lesson: String, @Field("topic") topic: String): Observable<Unit>

    @GET("subjects/{subject_id}/lessons")
    fun bySubjectId(@Path("subject_id") subject_id: String): Observable<List<Lesson>>
}