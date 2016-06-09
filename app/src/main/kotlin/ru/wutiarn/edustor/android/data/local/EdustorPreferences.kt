package ru.wutiarn.edustor.android.data.local

import android.content.SharedPreferences

class EdustorPreferences(private val androidPref: SharedPreferences) {
    var token: String?
        get() = androidPref.getString("token", null)
        set(value) {
            androidPref.edit().putString("token", value).commit()
        }
}