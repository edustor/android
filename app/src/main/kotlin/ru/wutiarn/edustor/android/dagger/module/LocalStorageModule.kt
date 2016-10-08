package ru.wutiarn.edustor.android.dagger.module

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
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
    fun edustorPreferences(objectMapper: ObjectMapper): EdustorPreferences {
        return EdustorPreferences(context, objectMapper)
    }

    @Provides
    @AppScope
    fun activeSession(prefs: EdustorPreferences, syncManager: SyncManager, pdfSyncManager: PdfSyncManager): ActiveSession {
        return ActiveSession(prefs, context, syncManager, pdfSyncManager)
    }

    @Provides
    @AppScope
    fun edustorConstants(context: Context): EdustorConstants {
        return EdustorConstants(context)
    }

    @Provides
    @AppScope
    fun syncManager(context: Context, prefs: EdustorPreferences, constants: EdustorConstants): SyncManager {
        return SyncManager(context, prefs, constants)
    }

    @Provides
    @AppScope
    fun pdfSyncManager(context: Context): PdfSyncManager {
        return PdfSyncManager(context)
    }
}
