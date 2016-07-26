package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import rx.Completable
import rx.Observable

class RealmLessonRepo() : LessonsRepo {
    override fun byUUID(uuid: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("documents.uuid", uuid)
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

    override fun reorderDocuments(lesson: String, documentId: String, afterDocumentId: String?): Completable {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .first()
                .map { lesson ->
                    realm.executeTransaction {
                        val document = lesson.documents.first { it.id == documentId }
                        lesson.documents.remove(document)

                        val targetIndex: Int

                        if (afterDocumentId != null) {
                            val afterDocument = lesson.documents.first { it.id == afterDocumentId }

                            targetIndex = lesson.documents.indexOf(afterDocument) + 1
                        } else {
                            targetIndex = 0
                        }

                        lesson.documents.add(targetIndex, document)
                        lesson.calculateDocumentIndexes()
                    }
                }.toCompletable()
    }

    override fun setTopic(lesson: String, topic: String): Completable {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .first()
                .map { lesson ->
                    realm.executeTransaction {
                        lesson.topic = if (topic.length != 0) topic else null
                    }
                }.toCompletable()
    }

}