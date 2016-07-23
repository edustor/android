package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId

open class User {
    lateinit var email: String
    var id: String = ObjectId.get().toString()
}