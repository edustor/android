package ru.wutiarn.edustor.android.data.models

import org.threeten.bp.LocalDate

/**
 * Created by wutiarn on 28.02.16.
 */
data class Lesson(
        var subject: Subject? = null,
        var date: LocalDate? = null,
        var topic: String? = null,
        var documents: MutableList<Document> = mutableListOf(),
        var id: String? = null
)