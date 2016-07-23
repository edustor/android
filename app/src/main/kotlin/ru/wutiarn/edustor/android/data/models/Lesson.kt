package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId
import org.threeten.bp.LocalDate

class Lesson() {
    lateinit var subject: Subject
    lateinit var date: LocalDate
    var topic: String? = null
    var documents: MutableList<Document> = mutableListOf()
    var id: String = ObjectId.get().toString()
}