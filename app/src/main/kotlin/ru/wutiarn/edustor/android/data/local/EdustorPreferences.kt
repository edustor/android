package ru.wutiarn.edustor.android.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class EdustorPreferences(context: Context) {
    private val pref = context.getSharedPreferences("ru.wutiarn.edustor", Application.MODE_PRIVATE)

    var token: String? by PrefDelegate(pref)
    var firebaseToken: String? by PrefDelegate(pref)


    private class PrefDelegate(private val androidPref: SharedPreferences) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return androidPref.getString(property.name, null)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            androidPref.edit().putString(property.name, value).apply()
        }
    }
}