package ru.wutiarn.edustor.android.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Account() : RealmObject() {
    @PrimaryKey open var id: String = UUID.randomUUID().toString()

    @Suppress("LeakingThis")
    constructor(dto: AccountDTO) : this() {
        id = dto.id
    }

    data class AccountDTO(
            val id: String
    )
}