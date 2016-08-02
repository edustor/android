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
open class Lesson() : RealmObject() {
    open lateinit var subject: Subject
    @Ignore open var date: LocalDate = LocalDate.ofEpochDay(0)
        get() = LocalDate.ofEpochDay(realmDate)
        set(value) {
            field = value
            realmDate = value.toEpochDay()
        }
    open var topic: String? = null
    open var documents: RealmList<Document> = RealmList()
    @PrimaryKey open var id: String = UUID.randomUUID().toString()

    @JsonIgnore var realmDate: Long = 0

    @Ignore var syncStatus: PdfSyncStatus? = null

    constructor(subject: Subject, realmDate: Long) : this() {
        this.subject = subject
        this.realmDate = realmDate
    }

    fun calculateDocumentIndexes() {
        IntRange(0, documents.lastIndex)
                .forEach { documents[it].index = it }
    }
}
