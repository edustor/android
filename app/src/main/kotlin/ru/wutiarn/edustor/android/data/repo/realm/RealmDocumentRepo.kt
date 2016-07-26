package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import rx.Completable
import rx.Observable

class RealmDocumentRepo : DocumentRepo {
    override fun activateUUID(uuid: String, lesson: String, offset: Int, instant: Instant): Observable<Document> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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