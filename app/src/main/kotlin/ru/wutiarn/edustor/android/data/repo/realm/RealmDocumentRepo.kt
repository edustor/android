package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import rx.Completable
import rx.Single

class RealmDocumentRepo(val lessonRepo: LessonsRepo) : DocumentRepo {
    override fun activateUUID(uuid: String, lessonId: String, instant: Instant): Single<Document> {
        val realm = Realm.getDefaultInstance()
        return lessonRepo.byId(lessonId)
                .first()
                .map { lesson ->
                    realm.executeTransaction {
                        val targetIndex = lesson.documents.max("index").toInt() + 1
                        val document = Document(uuid, instant, targetIndex)
                        realm.copyToRealm(document)
                        lesson.documents.add(document)
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
                    realm.executeTransaction({ doc.deleteFromRealm() })
                }
                .toCompletable()
    }
}