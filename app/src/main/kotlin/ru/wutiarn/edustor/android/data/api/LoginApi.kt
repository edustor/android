package ru.wutiarn.edustor.android.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.wutiarn.edustor.android.data.models.Session
import rx.Observable

interface LoginApi {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("token") token: String): Observable<Session>
}