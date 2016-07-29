package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import rx.Completable
import rx.Single

class RealmDocumentRepo(val lessonRepo: LessonsRepo, val syncTasksManager: SyncManager) : DocumentRepo {
    override fun activateUUID(uuid: String, lessonId: String, instant: Instant): Single<Document> {
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

                        val syncTask = SyncTask("documents/uuid/activate", mapOf(
                                "uuid" to document.uuid,
                                "lesson" to lesson.id,
                                "instant" to document.realmTimestamp
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
                    return@map lesson.documents.first { it.uuid == uuid }
                }.toSingle()
    }

    override fun delete(documentId: String): Completable {
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
                .toCompletable()
    }
}