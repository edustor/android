package ru.wutiarn.edustor.android.data.api

import retrofit2.http.GET
import ru.wutiarn.edustor.android.data.models.Subject
import rx.Observable

/**
 * Created by wutiarn on 13.03.16.
 */
interface SubjectsApi {
    @GET("subjects/list")
    fun list(): Observable<List<Subject>>
}