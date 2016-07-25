package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import okhttp3.ResponseBody
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import rx.Observable

class RealmLessonRepo() : LessonsRepo {
    override fun byUUID(uuid: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("uuid", uuid)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
    }

    override fun byDate(subject: String, epochDay: Long): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("realmDate", epochDay)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
    }

    override fun byId(id: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
    }

    override fun bySubjectId(subject_id: String): Observable<List<Lesson>> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("subject.id", subject_id)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList() }
    }

    override fun reorderDocuments(lesson: String, document: String, after: String?): Observable<ResponseBody> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTopic(lesson: String, topic: String): Observable<ResponseBody> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}