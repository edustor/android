package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import rx.Observable

class RealmDocumentRepo : DocumentRepo {
    override fun activateUUID(uuid: String, lesson: String, offset: Int, instant: Instant): Observable<Document> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(documentId: String): Observable<Unit> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("documents.id", documentId)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .first()
                .map {
                    realm.beginTransaction()
                    val document = it.first().documents.first { it.id == documentId }
                    it.map { it.documents.remove(document) }
                    document.deleteFromRealm()
                    realm.commitTransaction()
                }
    }
}