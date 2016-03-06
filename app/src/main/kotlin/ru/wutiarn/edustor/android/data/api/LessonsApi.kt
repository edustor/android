package ru.wutiarn.edustor.android.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

/**
 * Created by wutiarn on 02.03.16.
 */
interface LessonsApi {

    @GET("lessons/uuid/{uuid}")
    fun byUUID(@Path("uuid") uuid: String): Observable<Lesson>
}