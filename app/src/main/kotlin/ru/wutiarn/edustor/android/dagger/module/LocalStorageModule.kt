package ru.wutiarn.edustor.android.dagger.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.ActiveSession
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

@Module
class LocalStorageModule(val context: Context) {
    @Provides
    @AppScope
    fun context(): Context {
        return context
    }

    @Provides
    @AppScope
    fun edustorPreferences(): EdustorPreferences {
        return EdustorPreferences(context)
    }

    @Provides
    @AppScope
    fun activeSession(prefs: EdustorPreferences): ActiveSession {
        return ActiveSession(prefs, context)
    }
}
