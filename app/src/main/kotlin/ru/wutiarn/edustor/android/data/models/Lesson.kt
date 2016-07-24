package ru.wutiarn.edustor.android.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.bson.types.ObjectId
import org.threeten.bp.LocalDate

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
    @PrimaryKey open var id: String = ObjectId.get().toString()

    @JsonIgnore
    private var realmDate: Long = 0
}
