package ru.wutiarn.edustor.android

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.wutiarn.edustor.android.data.repository.DocumentsRepository
import rx.lang.kotlin.onError
import rx.schedulers.Schedulers

class DocumentsService {
    val retrofit: Retrofit

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor { addHeaders(it) }
                .build()

        retrofit = Retrofit.Builder()
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

    fun activateUUID(uuid: String) {
        val docRepo = retrofit.create(DocumentsRepository::class.java)
        docRepo.activateUUID(uuid, offset = 3)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .onError { Log.e("UUID RESULT", "Network error", it) }
                .subscribe { Log.i("UUID RESULT", "Response: ${it.string()}") }
    }
}