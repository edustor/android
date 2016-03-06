package ru.wutiarn.edustor.android

import okhttp3.*
import org.junit.Test
import org.mockito.Mockito
import ru.wutiarn.edustor.android.dagger.component.DaggerAppComponent
import ru.wutiarn.edustor.android.dagger.module.RetrofitModule
import rx.observers.TestSubscriber

/**
 * Created by wutiarn on 05.03.16.
 */
class RetrofitTest {
    @Test
    fun uuidInfo() {
        val retrofitModule = RetrofitModule()

        val spyModule = Mockito.spy(retrofitModule)

        Mockito.`when`(spyModule.httpClient()).thenReturn(getFakeHttpClient("""{"owner":{"login":"user","id":"56d3279ba826458c66c707b3"},"uuid":"18e69f5b-5a97-4ce7-9692-23ea18155be3","timestamp":1457257415.179000000,"id":"56dbfbc7a826814a434d4add","uploaded":false}"""));

        val repository = DaggerAppComponent.builder().retrofitModule(spyModule).build().documentsApi
        val subscriber = TestSubscriber<String>()

        repository.UUIDInfo("18e69f5b-5a97-4ce7-9692-23ea18155be3")
                .map { it.id }
                .subscribe(subscriber)

        subscriber.assertNoErrors()
        subscriber.assertValue("56dbfbc7a826814a434d4add")
    }

    fun getFakeHttpClient(responseStr: String): OkHttpClient =
            OkHttpClient.Builder().addInterceptor(getInterceptor(responseStr)).build()


    fun getInterceptor(responseStr: String) = Interceptor {
        Response.Builder()
                .code(200)
                .message(responseStr)
                .request(it.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseStr.toByteArray()))
                .addHeader("content-type", "application/json")
                .build()
    }
}