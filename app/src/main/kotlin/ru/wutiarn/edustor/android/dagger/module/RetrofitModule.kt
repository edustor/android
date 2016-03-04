package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.wutiarn.edustor.android.dagger.annotation.AppScope

/**
 * Created by wutiarn on 03.03.16.
 */
@Module
class RetrofitModule {
    @Provides
    @AppScope
    fun retrofitClient(): Retrofit {
        val client = OkHttpClient.Builder()
                .addInterceptor { addHeaders(it) }
                .build()

        return Retrofit.Builder()
                .client(client)
                .baseUrl("http://192.168.10.3:8080/api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun addHeaders(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
                .header("token", "a7933bb1-7d01-4db0-91b6-419412dd85c9")
                .build()

        return chain.proceed(request)
    }
}