package ru.wutiarn.edustor.android.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Tag() : RealmObject() {
    open lateinit var name: String
    @PrimaryKey open var id: String = UUID.randomUUID().toString()

    @Suppress("LeakingThis")
    constructor(dto: TagDTO) : this() {
        this.id = dto.id
        this.name = dto.name
    }

    data class TagDTO(
            val id: String,
            val owner: String,
            val name: String,
            val removed: Boolean
    )
}