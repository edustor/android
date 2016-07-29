package ru.wutiarn.edustor.android.data.models

open class Session {
    open lateinit var user: User
    open lateinit var token: String
}