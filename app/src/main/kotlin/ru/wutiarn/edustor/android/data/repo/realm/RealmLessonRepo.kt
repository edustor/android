package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import ru.wutiarn.edustor.android.util.extension.withSyncStatus

class RealmLessonRepo(val syncTasksManager: SyncManager,
                      val pdfSyncManager: PdfSyncManager) : LessonsRepo {
    override fun byQR(qr: String): Lesson? {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("pages.qr", qr)
                .findFirst()
                ?.copyFromRealm<Lesson>()
                ?.withSyncStatus(pdfSyncManager)
    }

    override fun byDate(tag: String, epochDay: Long): Lesson {
        val lesson = Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("tag.id", tag)
                .equalTo("realmDate", epochDay)
                .findFirst() ?: let {
            Realm.getDefaultInstance().use {
                it.where(Tag::class.java)
                        .equalTo("id", tag)
                        .findFirst()
                        .let { tag ->
                            var lesson = Lesson(tag, epochDay)
                            Realm.getDefaultInstance().executeTransaction {
                                lesson = it.copyToRealm(lesson)
                            }

                            val syncTask = SyncTask("lessons/create", mapOf(
                                    "id" to lesson.id,
                                    "date" to lesson.date.toEpochDay(),
                                    "tag" to tag
                            ))
                            syncTasksManager.addTask(syncTask)

                            lesson
                        }
            }
        }
                .copyFromRealm<Lesson>()
                .withSyncStatus(pdfSyncManager)
        return lesson
    }

    override fun byId(id: String): Lesson {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("id", id)
                .findFirst()
                .copyFromRealm<Lesson>()
                .withSyncStatus(pdfSyncManager)
    }

    override fun byTagId(tagId: String): List<Lesson> {
        return Realm.getDefaultInstance().where(Lesson::class.java)
                .equalTo("tag.id", tagId)
                .findAll()
                .map { it.copyFromRealm<Lesson>().withSyncStatus(pdfSyncManager) }
    }

    override fun reorderPages(lesson: String, pageId: String, afterPageId: String?) {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirst()
                .let { lesson ->
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

    override fun setTopic(lesson: String, topic: String) {
        val realm = Realm.getDefaultInstance()
        return realm.where(Lesson::class.java)
                .equalTo("id", lesson)
                .findFirst()
                .let { lesson ->
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