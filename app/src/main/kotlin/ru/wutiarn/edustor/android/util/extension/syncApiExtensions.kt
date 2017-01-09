package ru.wutiarn.edustor.android.util.extension

import android.util.Log
import io.realm.Realm
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Page
import ru.wutiarn.edustor.android.data.models.Subject
import rx.Observable

fun SyncApi.fullSyncNow(): Observable<Unit> {
    return this.fetch()
            .map { initData ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    realm.delete(Page::class.java)
                    realm.delete(Lesson::class.java)
                    realm.delete(Subject::class.java)

                    initData.lessons.forEach(Lesson::calculatePageIndexes)

                    realm.copyToRealmOrUpdate(initData.user)
                    realm.copyToRealmOrUpdate(initData.subjects)
                    realm.copyToRealmOrUpdate(initData.lessons)
                }

                Log.i("SyncApi", "Full sync finished")
                Unit
            }
}