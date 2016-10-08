package ru.wutiarn.edustor.android.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import kotlin.reflect.KProperty

class EdustorPreferences(context: Context, private val objectMapper: ObjectMapper) {
    private val pref = context.getSharedPreferences("ru.wutiarn.edustor", Application.MODE_PRIVATE)

    var syncTasks: List<SyncTask> by getDelegate(object : TypeReference<List<SyncTask>>() {}, "[]")

    fun clear() {
        pref.edit().clear().commit()
    }

    fun <T> getDelegate(type: TypeReference<T>, defaultValue: String = "null"): JsonPrefDelegate<T> {
        return JsonPrefDelegate(pref, objectMapper, type, defaultValue)
    }

    fun <T> getDelegate(clazz: Class<T>, defaultValue: String = "null"): JsonPrefDelegate<T> {
        return JsonPrefDelegate(pref, objectMapper, clazz, defaultValue)
    }

    open private class PrefDelegate(private val androidPref: SharedPreferences) {
        open operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return androidPref.getString(property.name, null)
        }

        open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            androidPref.edit().putString(property.name, value).apply()
        }
    }

    class JsonPrefDelegate<T> {

        private var androidPref: SharedPreferences
        private var objectMapper: ObjectMapper
        private var defaultValue: String = "null"
        private var type: TypeReference<T>? = null
        private var clazz: Class<T>? = null

        constructor(androidPref: SharedPreferences,
                    objectMapper: ObjectMapper,
                    type: TypeReference<T>,
                    defaultValue: String = "null") {
            this.androidPref = androidPref
            this.objectMapper = objectMapper
            this.defaultValue = defaultValue

            this.type = type
        }

        constructor(androidPref: SharedPreferences,
                    objectMapper: ObjectMapper,
                    clazz: Class<T>,
                    defaultValue: String = "null") {
            this.androidPref = androidPref
            this.objectMapper = objectMapper
            this.defaultValue = defaultValue

            this.clazz = clazz

        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val str = androidPref.getString(property.name, defaultValue)
            if (clazz != null) {
                return objectMapper.readValue<T>(str, clazz)
            } else {
                return objectMapper.readValue<T>(str, type)
            }
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            val str = objectMapper.writeValueAsString(value)
            androidPref.edit().putString(property.name, str).apply()
        }
    }
}