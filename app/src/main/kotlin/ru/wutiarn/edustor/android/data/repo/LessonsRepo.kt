package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Lesson

interface LessonsRepo {
    fun byQR(qr: String): Lesson?
    fun byDate(tag: String, epochDay: Long): Lesson
    fun byId(id: String): Lesson
    fun reorderPages(lesson: String, pageId: String, afterPageId: String?)
    fun setTopic(lesson: String, topic: String)
    fun byTagId(tagId: String): List<Lesson>
}