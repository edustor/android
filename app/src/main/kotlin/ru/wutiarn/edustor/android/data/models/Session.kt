package ru.wutiarn.edustor.android.data.models

open class Session {
    lateinit var user: User
    open lateinit var token: String
}