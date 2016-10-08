package ru.wutiarn.edustor.android.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.wutiarn.edustor.android.data.models.OAuthTokenResult
import rx.Observable

interface AccountsApi {
    @FormUrlEncoded
    @POST("oauth2/token")
    fun token(
            @Field("grant_type") grantType: String,
            @Field("refresh_token") refreshToken: String? = null,
            @Field("username") username: String? = null,
            @Field("password") password: String? = null,
            @Field("scope") scope: String? = null
    ): Observable<OAuthTokenResult>
}