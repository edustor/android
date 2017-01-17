package ru.wutiarn.edustor.android.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import java.util.*

@RealmClass
open class Lesson() : RealmObject(), MainListEntity {
    open lateinit var tag: Tag
    @Ignore open var date: LocalDate = LocalDate.ofEpochDay(0)
        get() = LocalDate.ofEpochDay(realmDate)
        set(value) {
            field = value
            realmDate = value.toEpochDay()
        }
    open var topic: String? = null
    open var pages: RealmList<Page> = RealmList()
    @PrimaryKey open var id: String = UUID.randomUUID().toString()

    @JsonIgnore var realmDate: Long = 0

    @Ignore var syncStatus: PdfSyncStatus? = null

    @Suppress("LeakingThis")
    constructor(tag: Tag, realmDate: Long) : this() {
        this.tag = tag
        this.realmDate = realmDate
    }

    @Suppress("LeakingThis")
    constructor(dto: LessonDTO, tag: Tag) : this() {
        this.id = dto.id
        this.topic = dto.topic
        this.date = dto.date
        this.pages = dto.pages
                .map(::Page)
                .toTypedArray()
                .let { RealmList<Page>(*it) }

        this.tag = tag
    }

    fun calculatePageIndexes() {
        IntRange(0, pages.lastIndex)
                .forEach { pages[it].index = it }
    }

    data class LessonDTO(
            val id: String,
            val owner: String,
            val tag: String,
            val topic: String?,
            val date: LocalDate,
            val removed: Boolean,
            val pages: List<Page.PageDTO>
    )
}
