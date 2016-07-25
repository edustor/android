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

    override fun reorderDocuments(lesson: String, documentId: String, afterDocumentId: String?): Observable<Unit> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .first()
                .map {
                    realm.beginTransaction()

                    val document = it.documents.first { it.id == documentId }
                    it.documents.remove(document)

                    val targetIndex: Int

                    if (afterDocumentId != null) {
                        val afterDocument = it.documents.first { it.id == afterDocumentId }

                        targetIndex = it.documents.indexOf(document) + 1
                    } else {
                        targetIndex = 0
                    }

                    it.documents.add(targetIndex, document)
                    realm.commitTransaction()
                }
    }

    override fun setTopic(lesson: String, topic: String): Observable<Unit> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .first()
                .map {
                    realm.beginTransaction()
                    it.topic =  if (topic.length != 0) topic else null
                    realm.commitTransaction()
                }
    }

}