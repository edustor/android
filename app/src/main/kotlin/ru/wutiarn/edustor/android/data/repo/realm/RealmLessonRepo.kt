package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import rx.Observable

class RealmLessonRepo(val syncTasksManager: SyncManager) : LessonsRepo {
    override fun byUUID(uuid: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("documents.uuid", uuid)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .copyFromRealm()
    }

    override fun byDate(subject: String, epochDay: Long): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("subject.id", subject)
                .equalTo("realmDate", epochDay)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .flatMap {
                    if (it.isValid) return@flatMap Observable.just(it)

                    return@flatMap Realm.getDefaultInstance().where(Subject::class.java)
                            .equalTo("id", subject)
                            .findFirstAsync()
                            .asObservable<Subject>()
                            .filter { it.isLoaded }
                            .map {
                                val lesson = Lesson(it, epochDay)
                                Realm.getDefaultInstance().executeTransaction {
                                    it.copyToRealm(lesson)
                                }
                                lesson
                            }
                }
                .copyFromRealm()
    }

    override fun byId(id: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .copyFromRealm()
    }

    override fun bySubjectId(subject_id: String): Observable<List<Lesson>> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("subject.id", subject_id)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList().map { it.copyFromRealm<Lesson>() } }
//                .flatMap { it.toObservable().flatMap { Observable.just(it).setUpSyncState(pdfSyncManager).toList() } }
    }

    override fun reorderDocuments(lesson: String, documentId: String, afterDocumentId: String?): Observable<Unit> {
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
                        val syncTask = SyncTask("lessons/date/documents/reorder", mapOf(
                                "subject" to lesson.subject.id,
                                "date" to lesson.realmDate,
                                "document" to document.id,
                                "after" to afterDocumentId
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
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
                .map { lesson ->
                    realm.executeTransaction {
                        lesson.topic = if (topic.length != 0) topic else null
                        val syncTask = SyncTask("lessons/date/topic/put", mapOf(
                                "topic" to lesson.topic,
                                "subject" to lesson.subject.id,
                                "date" to lesson.realmDate
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
                }
    }
}