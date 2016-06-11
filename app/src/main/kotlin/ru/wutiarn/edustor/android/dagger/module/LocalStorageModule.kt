package ru.wutiarn.edustor.android.dagger.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.ActiveSession
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

@Module
class LocalStorageModule(val androidPref: SharedPreferences, val application: EdustorApplication) {
    @Provides
    @AppScope
    fun edustorPreferences(): EdustorPreferences {
        return EdustorPreferences(androidPref)
    }

    @Provides
    @AppScope
    fun activeSession(prefs: EdustorPreferences): ActiveSession {
        return ActiveSession(prefs, application)
    }
}
