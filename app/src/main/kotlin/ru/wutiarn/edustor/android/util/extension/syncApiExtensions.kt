package ru.wutiarn.edustor.android.util.extension

import android.util.Log
import io.realm.Realm
import ru.wutiarn.edustor.android.data.api.SyncApi
import rx.Observable

fun SyncApi.fullSyncNow(): Observable<Unit> {
    return this.fetch()
            .configureAsync()
            .map { initData ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    realm.deleteAll()

                    initData.lessons.forEach {
                        it.calculateDocumentIndexes()
                    }

                    realm.copyToRealm(initData.user)
                    realm.copyToRealm(initData.subjects)
                    realm.copyToRealmOrUpdate(initData.lessons)
                }

                Log.i("SyncApi", "Sync finished")
                Unit
            }
}