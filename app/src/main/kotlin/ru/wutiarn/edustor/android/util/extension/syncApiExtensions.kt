package ru.wutiarn.edustor.android.util.extension

import android.util.Log
import io.realm.Realm
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.models.Account
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Page
import ru.wutiarn.edustor.android.data.models.Tag
import rx.Observable

fun SyncApi.fullSyncNow(): Observable<Unit> {
    return this.fetch()
            .map { initData ->

                val account = Account(initData.account)
                val tags = initData.tags.map(::Tag).sortedBy(Tag::id)

                val lessons = initData.lessons.map { lessonDTO ->
                    val tag = tags.first { it.id == lessonDTO.tag }  // TODO: Replace with binary search
                    Lesson(lessonDTO, tag)
                }

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    realm.delete(Page::class.java)
                    realm.delete(Lesson::class.java)
                    realm.delete(Tag::class.java)

                    lessons.forEach(Lesson::calculatePageIndexes)

                    realm.copyToRealmOrUpdate(account)
                    realm.copyToRealmOrUpdate(tags)
                    realm.copyToRealmOrUpdate(lessons)
                }

                Log.i("SyncApi", "Full sync finished")
                Unit
            }
}