package ru.wutiarn.edustor.android.util.extension

import android.content.Context
import io.realm.Realm
import io.realm.RealmObject
import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable
import java.io.File

fun Observable<Lesson>.setUpSyncStateAsync(pdfSyncManager: PdfSyncManager): Observable<Lesson> {
    return this
            .flatMap { lesson ->
                val syncStatusObservable = pdfSyncManager.getSyncStatusAsync(lesson.id)
                return@flatMap syncStatusObservable.map { lesson.syncStatus = it; lesson }
            }
}

fun Lesson.getPdfUrl(baseUrl: String): String {
    return "$baseUrl${this.id}"
}

fun Lesson.getCacheFile(context: Context): File {
    val file = File(context.externalCacheDir, "pdf/${this.id}.pdf")
    return file
}

fun <T : RealmObject> Observable<T>.copyFromRealm(): Observable<T> {
    return this.map { obj ->
        obj?.let {
            obj.copyFromRealm<T>()
        }
    }
}

fun <T : RealmObject> RealmObject.copyFromRealm(): T {
    return Realm.getDefaultInstance().use {
        Realm.getDefaultInstance().use {
            @Suppress("UNCHECKED_CAST")
            (it.copyFromRealm(this) as T)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : RealmObject> RealmObject.copyToRealm(): T {
    return Realm.getDefaultInstance().use {
        Realm.getDefaultInstance().use {
            var result: T? = null
            it.executeTransaction {
                result = (it.copyToRealmOrUpdate(this) as T)
            }
            result!!
        }
    }
}