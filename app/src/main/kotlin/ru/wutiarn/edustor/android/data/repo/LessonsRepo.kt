package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Lesson
import rx.Observable

interface LessonsRepo {
    fun byQR(qr: String): Observable<Lesson>
    fun byDate(subject: String, epochDay: Long): Observable<Lesson>
    fun byId(id: String): Observable<Lesson>
    fun reorderDocuments(lesson: String, documentId: String, afterDocumentId: String?): Observable<Unit>
    fun setTopic(lesson: String, topic: String): Observable<Unit>
    fun bySubjectId(subject_id: String): Observable<List<Lesson>>
}