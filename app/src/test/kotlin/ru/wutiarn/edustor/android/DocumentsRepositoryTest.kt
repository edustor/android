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
class DocumentsRepositoryTest {
    @Test
    fun uuidInfo() {
        val retrofitModule = RetrofitModule()

        val spyModule = Mockito.spy(retrofitModule)

        Mockito.`when`(spyModule.httpClient()).thenReturn(getFakeHttpClient("""{"owner":{"login":"user","id":"56d3279ba826458c66c707b3"},"lesson":{"subject":{"name":"Алгебра","year":10,"id":"56d340d1a826427ae8dc5f81"},"start":[0,0],"end":[23,59],"date":[2016,3,2],"id":"56d6d919a8269ff3e0840c43"},"uuid":"18e69f5b-5a97-4ce7-9692-23ea18155be3","timestamp":1456920857.173000000,"id":"56d6d919a8269ff3e0840c44","uploaded":true}"""));

        val repository = DaggerAppComponent.create().documentsRepository
        val subscriber = TestSubscriber<String>()

        repository.documentUUIDInfo("18e69f5b-5a97-4ce7-9692-23ea18155be3")
                .map { it.id }
                .subscribe(subscriber)

        subscriber.assertNoErrors()
        subscriber.assertValue("56d6d919a8269ff3e0840c44")
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