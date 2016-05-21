package ru.wutiarn.edustor.android.data.models

import org.threeten.bp.LocalDate

data class Lesson(
        var subject: Subject? = null,
        var date: LocalDate? = null,
        var topic: String? = null,
        var documents: MutableList<Document> = mutableListOf(),
        var id: String? = null
)