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
    override fun byQR(qr: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("pages.qr", qr)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
    }

    override fun byDate(subject: String, epochDay: Long): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("subject.id", subject)
                .equalTo("realmDate", epochDay)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .flatMap { found ->
                    if (found.isNotEmpty()) {
                        Observable.just(found.first())
                    } else {
                        Realm.getDefaultInstance().use {
                            it.where(Subject::class.java)
                                    .equalTo("id", subject)
                                    .findFirstAsync()
                                    .asObservable<Subject>()
                                    .filter { it.isLoaded }
                                    .map {
                                        var lesson = Lesson(it, epochDay)
                                        Realm.getDefaultInstance().executeTransaction {
                                            lesson = it.copyToRealm(lesson)
                                        }

                                        val syncTask = SyncTask("lessons/create", mapOf(
                                                "id" to lesson.id,
                                                "date" to lesson.date.toEpochDay(),
                                                "subject" to subject
                                        ))
                                        syncTasksManager.addTask(syncTask)

                                        lesson
                                    }
                        }
                    }

                }
                .copyFromRealm()
    }

    override fun byId(id: String): Observable<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("id", id)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .map { it.first() }
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

    override fun reorderPages(lesson: String, pageId: String, afterPageId: String?): Observable<Unit> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirstAsync()
                .asObservable<Lesson>()
                .filter { it.isLoaded }
                .first()
                .map { lesson ->
                    realm.executeTransaction {
                        val page = lesson.pages.first { it.id == pageId }
                        lesson.pages.remove(page)

                        val targetIndex: Int

                        if (afterPageId != null) {
                            val afterPage = lesson.pages.first { it.id == afterPageId }

                            targetIndex = lesson.pages.indexOf(afterPage) + 1
                        } else {
                            targetIndex = 0
                        }

                        lesson.pages.add(targetIndex, page)
                        lesson.calculatePageIndexes()
                        val syncTask = SyncTask("lessons/pages/reorder", mapOf(
                                "lesson" to lesson.id,
                                "page" to page.id,
                                "after" to afterPageId
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
                        lesson.topic = if (topic.isNotEmpty()) topic else null
                        val syncTask = SyncTask("lessons/topic/put", mapOf(
                                "topic" to lesson.topic,
                                "lesson" to lesson.id
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
                }
    }
}