package ru.wutiarn.edustor.android.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.*

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
    fun activeSession(prefs: EdustorPreferences, syncManager: SyncManager): ActiveSession {
        return ActiveSession(prefs, context, syncManager)
    }

    @Provides
    @AppScope
    fun edustorConstants(context: Context): EdustorConstants {
        return EdustorConstants(context)
    }

    @Provides
    @AppScope
    fun syncManager(context: Context): SyncManager {
        return SyncManager(context)
    }

    @Provides
    @AppScope
    fun pdfSyncManager(context: Context): PdfSyncManager {
        return PdfSyncManager(context)
    }
}
