package ru.wutiarn.edustor.android.data.models

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import ru.wutiarn.edustor.android.data.models.util.sync.TagSyncStatus
import java.util.*

@RealmClass
open class Tag() : RealmObject(), MainListEntity {
    @PrimaryKey open var id: String = UUID.randomUUID().toString()
    open lateinit var name: String
    open var parent: Tag? = null

    @Ignore var syncStatus: TagSyncStatus? = null

    @Suppress("LeakingThis")
    constructor(dto: TagDTO) : this() {
        this.id = dto.id
        this.name = dto.name
    }

    data class TagDTO(
            val id: String,
            val owner: String,
            val parent: String?,
            val name: String,
            val removed: Boolean
    )
}