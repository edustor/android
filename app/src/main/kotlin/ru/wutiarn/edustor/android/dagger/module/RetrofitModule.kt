package ru.wutiarn.edustor.android.dagger.module

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.threeten.bp.LocalDate
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.HttpException
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.AccountsApi
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.local.ActiveSession
import ru.wutiarn.edustor.android.data.local.EdustorConstants
import ru.wutiarn.edustor.android.data.models.OAuthTokenResult
import ru.wutiarn.edustor.android.util.ConversionUtils

@Module
open class RetrofitModule {
    @Provides
    @AppScope
    open fun httpClient(session: ActiveSession, objectMapper: ObjectMapper, edustorConstants: EdustorConstants): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor {
                    return@addInterceptor intercept(it, session, objectMapper, edustorConstants)
                }
                .build()
    }

    private fun intercept(it: Interceptor.Chain, session: ActiveSession, objectMapper: ObjectMapper, edustorConstants: EdustorConstants): Response? {
        val original = it.request()
        val request: Request
        if (session.isLoggedIn) {
            request = original.injectToken(session.token!!)
        } else {
            request = original
        }

        var result = it.proceed(request)

        if (result.code() == 401) {
            if (session.refreshToken != null) {
                val accountsApi = accountsApi(objectMapper, edustorConstants)
                val oauthRespObservable = accountsApi.token("refresh_token", refreshToken = session.refreshToken)
                val oauthResult: OAuthTokenResult
                try {
                    oauthResult = oauthRespObservable.toBlocking().first()
                } catch (e: HttpException) {
                    session.logout()
                    return result
                }
                session.setFromOAuthTokenResult(oauthResult)

                result = it.proceed(request.injectToken(session.token!!))

                if (result.code() == 401) {
                    session.logout()
                }
            } else {
                session.logout()
            }
        }

        return result
    }

    @Provides
    @AppScope
    fun retrofitClient(objectMapper: ObjectMapper, client: OkHttpClient, constants: EdustorConstants): Retrofit {
        return makeRetrofit(constants.core_url + "api/", client, objectMapper)
    }

    @Provides
    @AppScope
    fun objectMapper(): ObjectMapper {
        val conversionModule = SimpleModule("ru.edustor.datatype.custom", Version(1, 0, 0, null, "ru.edustor", "edustor"))
        conversionModule.addSerializer(ConversionUtils.LocalDateJsonSerializer())
        conversionModule.addDeserializer(LocalDate::class.java, ConversionUtils.LocalDateJsonDeserializer())

        return ObjectMapper()
                .registerModule(ThreeTenModule())
                .registerModule(conversionModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Provides
    @AppScope
    fun accountsApi(objectMapper: ObjectMapper, constants: EdustorConstants): AccountsApi {
        val retrofit = makeRetrofit(constants.accounts_url, OkHttpClient(), objectMapper)
        return retrofit.create(AccountsApi::class.java)
    }

    @Provides
    @AppScope
    fun syncApi(retrofit: Retrofit): SyncApi {
        return retrofit.create(SyncApi::class.java)
    }

    private fun makeRetrofit(baseUrl: String, client: OkHttpClient, objectMapper: ObjectMapper): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
    }

    private fun Request.injectToken(token: String): Request {
        return this.newBuilder()
                .header("token", token)
                .build()
    }
}