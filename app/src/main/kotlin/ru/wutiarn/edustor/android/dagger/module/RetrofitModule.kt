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
import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SubjectsApi
import javax.inject.Named

@Module
open class RetrofitModule {
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
    fun documentsApi(retrofit: Retrofit): DocumentsApi {
        return retrofit.create(DocumentsApi::class.java)
    }

    @Provides
    @AppScope
    fun lessonsApi(retrofit: Retrofit): LessonsApi {
        return retrofit.create(LessonsApi::class.java)
    }

    @Provides
    @AppScope
    fun subjectsApi(retrofit: Retrofit): SubjectsApi {
        return retrofit.create(SubjectsApi::class.java)
    }

    @Provides
    @AppScope
    fun loginApi(retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }
}