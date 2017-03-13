package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Page
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.PageRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import ru.wutiarn.edustor.android.util.extension.copyToRealm
import rx.Observable

class RealmPageRepo(val lessonRepo: LessonsRepo, val syncTasksManager: SyncManager) : PageRepo {
    override fun link(qr: String, lessonId: String, instant: Instant): Page {
        val realm = Realm.getDefaultInstance()
        return lessonRepo.byId(lessonId)
                .copyToRealm<Lesson>()
                .let { lesson ->
                    if (realm.where(Lesson::class.java).equalTo("pages.qr", qr).count() != 0L) {
                        throw IllegalArgumentException("QR already registered")
                    }

                    realm.executeTransaction {
                        val targetIndex = lesson.pages.max("index")?.toInt()?.plus(1) ?: 0
                        val page = Page(qr, instant, targetIndex)
                        realm.copyToRealm(page)
                        lesson.pages.add(page)

                        val syncTask = SyncTask("pages/link", mapOf(
                                "id" to page.id,
                                "qr" to page.qr,
                                "instant" to page.realmTimestamp,
                                "lesson" to lesson.id
                        ))
                        syncTasksManager.addTask(syncTask)
                    }
                    return@let lesson.pages.first { it.qr == qr }.copyFromRealm<Page>()
                }
    }


    override fun delete(pageId: String) {
        val realm = Realm.getDefaultInstance()
        return realm.where(Page::class.java)
                .equalTo("id", pageId)
                .findFirst()
                .let { doc ->
                    val syncTask = SyncTask("pages/delete", mapOf(
                            "page" to doc.id
                    ))
                    syncTasksManager.addTask(syncTask)
                    realm.executeTransaction({ doc.deleteFromRealm() })
                }
    }
}