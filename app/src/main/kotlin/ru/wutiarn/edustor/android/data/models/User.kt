package ru.wutiarn.edustor.android.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.bson.types.ObjectId

@RealmClass
open class User : RealmObject() {
    open lateinit var email: String
    @PrimaryKey
    open var id: String = ObjectId.get().toString()
}