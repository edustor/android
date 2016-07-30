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
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.local.ActiveSession
import ru.wutiarn.edustor.android.data.local.EdustorConstants
import ru.wutiarn.edustor.android.util.ConversionUtils

@Module
open class RetrofitModule {
    @Provides
    @AppScope
    open fun httpClient(session: ActiveSession): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor {
                    return@addInterceptor intercept(it, session)
                }
                .build()
    }

    private fun intercept(it: Interceptor.Chain, session: ActiveSession): Response? {
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
        return result
    }

    @Provides
    @AppScope
    fun retrofitClient(objectMapper: ObjectMapper, client: OkHttpClient, constants: EdustorConstants): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(constants.URL + "api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
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
    fun loginApi(retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @AppScope
    fun syncApi(retrofit: Retrofit): SyncApi {
        return retrofit.create(SyncApi::class.java)
    }
}