package ru.wutiarn.edustor.android.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
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

    @GET("lessons/{id}")
    fun byId(@Query("id") id: String): Observable<Lesson>
}