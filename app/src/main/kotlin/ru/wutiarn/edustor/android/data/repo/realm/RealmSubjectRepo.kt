package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.data.repo.SubjectsRepo
import rx.Observable

class RealmSubjectRepo() : SubjectsRepo {

    override val all: Observable<List<Subject>>
        get() {
            return Realm.getDefaultInstance().where(Subject::class.java)
                    .findAllAsync()
                    .asObservable()
                    .filter { it.isLoaded }
                    .map { it.toList() }
        }
}