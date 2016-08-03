package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import rx.Observable

class RealmDocumentRepo(val lessonRepo: LessonsRepo, val syncTasksManager: SyncManager) : DocumentRepo {
    override fun activateUUID(uuid: String, lessonId: String, instant: Instant): Observable<Document> {
        val realm = Realm.getDefaultInstance()
        return lessonRepo.byId(lessonId)
                .first()
                .map { lesson ->

                    if (realm.where(Lesson::class.java).equalTo("documents.uuid", uuid).count() != 0L) {
                        throw IllegalArgumentException("UUID already registered")
                    }

                    realm.executeTransaction {
                        val targetIndex = lesson.documents.max("index")?.toInt()?.plus(1) ?: 0
                        val document = Document(uuid, instant, targetIndex)
                        realm.copyToRealm(document)
                        lesson.documents.add(document)

                        val syncTask = SyncTask("documents/uuid/activate/date", mapOf(
                                "id" to document.id,
                                "uuid" to document.uuid,
                                "subject" to lesson.subject.id,
                                "date" to lesson.realmDate,
                                "instant" to document.realmTimestamp
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
                    return@map lesson.documents.first { it.uuid == uuid }.copyFromRealm<Document>()
                }
    }

    override fun delete(documentId: String): Observable<Unit> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Document::class.java)
                .equalTo("id", documentId)
                .findFirstAsync()
                .asObservable<Document>()
                .filter { it.isLoaded }
                .first()
                .map { doc ->
                    val syncTask = SyncTask("documents/delete", mapOf(
                            "document" to doc.id
                    ))
                    syncTasksManager.addTask(syncTask)
                    realm.executeTransaction({ doc.deleteFromRealm() })
                }
    }
}