package ru.wutiarn.edustor.android.data.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by wutiarn on 28.02.16.
 */
data class Lesson(
        var subject: Subject? = null,
        var start: LocalTime? = null,
        var end: LocalTime? = null,
        var date: LocalDate? = null,
        var documents: MutableList<Document> = mutableListOf(),
        var id: String? = null
)