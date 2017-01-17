package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

interface LessonsRepo {
    fun byQR(qr: String): Observable<Lesson>
    fun byDate(tag: String, epochDay: Long): Observable<Lesson>
    fun byId(id: String): Observable<Lesson>
    fun reorderPages(lesson: String, pageId: String, afterPageId: String?): Observable<Unit>
    fun setTopic(lesson: String, topic: String): Observable<Unit>
    fun byTagId(tagId: String): Observable<List<Lesson>>
}