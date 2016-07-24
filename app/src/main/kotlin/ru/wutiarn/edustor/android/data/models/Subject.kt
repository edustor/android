package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId

open class Subject {
    lateinit var name: String
    var id: String = ObjectId.get().toString()
}