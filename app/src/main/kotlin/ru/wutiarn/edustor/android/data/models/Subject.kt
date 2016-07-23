package ru.wutiarn.edustor.android.data.models

import org.bson.types.ObjectId

class Subject {
    lateinit var name: String
    var id: String = ObjectId.get().toString()
}