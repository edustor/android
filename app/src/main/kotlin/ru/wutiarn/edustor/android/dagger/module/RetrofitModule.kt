package ru.wutiarn.edustor.android.dagger.module

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.local.ActiveSession
import javax.inject.Named

@Module
open class RetrofitModule {
    @Provides
    @AppScope
    open fun httpClient(session: ActiveSession): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor {
                    val original = it.request()
                    val request: Request
                    if (session.isLoggedIn) {
                        request = original.newBuilder()
                                .header("token", session.token)
                                .build()
                    } else {
                        request = original
                    }

                    val result = it.proceed(request)
                    return@addInterceptor result
                }
                .build()
    }

    @Provides
    @AppScope
    fun retrofitClient(objectMapper: ObjectMapper, client: OkHttpClient, @Named("EDUSTOR_URL") url: String): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(url + "api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
    }

    @Provides
    @AppScope
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
                .registerModule(ThreeTenModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Provides
    @AppScope
    fun loginApi(retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @AppScope
    fun syncApi(retrofit: Retrofit): SyncApi {
        return retrofit.create(SyncApi::class.java)
    }
}