package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.data.repo.SubjectsRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import rx.Observable

class RealmSubjectRepo : SubjectsRepo {

    override val all: Observable<List<Tag>>
        get() {
            return Realm.getDefaultInstance().where(Tag::class.java)
                    .findAllAsync()
                    .asObservable()
                    .filter { it.isLoaded }
                    .map { it.toList().map { it.copyFromRealm<Tag>() } }
        }
}