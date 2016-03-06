package ru.wutiarn.edustor.android.dagger.module

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import ru.wutiarn.edustor.android.data.api.LessonsApi
import javax.inject.Named

/**
 * Created by wutiarn on 03.03.16.
 */
@Module
open class RetrofitModule {
    @Provides
    @AppScope
    @Named("API_URL")
    open fun url(): String {
        return "http://192.168.10.3:8080/api/"
    }

    @Provides
    @AppScope
    open fun httpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor {
                    val original = it.request()

                    val request = original.newBuilder()
                            .header("token", "a7933bb1-7d01-4db0-91b6-419412dd85c9")
                            .build()

                    return@addInterceptor it.proceed(request)
                }
                .build()
    }

    @Provides
    @AppScope
    fun retrofitClient(objectMapper: ObjectMapper, client: OkHttpClient, @Named("API_URL") url: String): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(url)
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
    fun documentsApi(retrofit: Retrofit): DocumentsApi {
        return retrofit.create(DocumentsApi::class.java)
    }

    @Provides
    @AppScope
    fun lessonsApi(retrofit: Retrofit): LessonsApi {
        return retrofit.create(LessonsApi::class.java)
    }
}