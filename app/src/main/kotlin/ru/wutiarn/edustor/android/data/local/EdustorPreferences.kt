package ru.wutiarn.edustor.android.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import kotlin.reflect.KProperty

class EdustorPreferences(context: Context) {
    private val pref = context.getSharedPreferences("ru.wutiarn.edustor", Application.MODE_PRIVATE)
    private val objectMapper = ObjectMapper()

    var token: String? by PrefDelegate(pref)

    var syncTasks: List<SyncTask> by JsonPrefDelegate(pref, objectMapper, object : TypeReference<List<SyncTask>>() {})

    fun clear() {
        pref.edit().clear().commit()
    }

    open private class PrefDelegate(private val androidPref: SharedPreferences) {
        open operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return androidPref.getString(property.name, null)
        }

        open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            androidPref.edit().putString(property.name, value).apply()
        }
    }

    private class JsonPrefDelegate<T>(private val androidPref: SharedPreferences,
                                      val objectMapper: ObjectMapper,
                                      val type: TypeReference<T>
    ) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val str = androidPref.getString(property.name, "[]")
            return objectMapper.readValue<T>(str, type)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            val str = objectMapper.writeValueAsString(value)
            androidPref.edit().putString(property.name, str).apply()
        }
    }
}