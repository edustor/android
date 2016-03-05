package ru.wutiarn.edustor.android

import org.junit.Test
import ru.wutiarn.edustor.android.dagger.module.RetrofitModule
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.repository.DocumentsRepository
import rx.lang.kotlin.toObservable
import rx.observers.TestSubscriber

/**
 * Created by wutiarn on 05.03.16.
 */
class DocumentsRepositoryTest {
    @Test
    fun uuidInfo() {
        val retrofitModule = RetrofitModule()
        val repository = DocumentsRepository(retrofitModule.documentsApi(retrofitModule.retrofitClient(retrofitModule.objectMapper(),
                url = "http://127.0.0.1:8080/api/")))

        val subscriber = TestSubscriber<Document>()

        repository.documentUUIDInfo("18e69f5b-5a97-4ce7-9692-23ea18155be3")
                .subscribe(subscriber)

        subscriber.assertValueCount(1)
        subscriber.onNextEvents.toObservable()
                .forEach { assert(it.id == "56d6d919a8269ff3e0840c44") }
    }
}