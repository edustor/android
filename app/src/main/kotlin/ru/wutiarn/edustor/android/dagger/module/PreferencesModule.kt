package ru.wutiarn.edustor.android.dagger.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

@Module
class PreferencesModule(val androidPref: SharedPreferences) {
    @Provides
    @AppScope
    fun edustorPreferences(): EdustorPreferences {
        return EdustorPreferences(androidPref)
    }
}
