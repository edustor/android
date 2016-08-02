package ru.wutiarn.edustor.android.dagger.component

import android.content.Context
import com.squareup.otto.Bus
import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.groups.ApiGroup
import ru.wutiarn.edustor.android.dagger.groups.RepoGroup
import ru.wutiarn.edustor.android.dagger.module.*
import ru.wutiarn.edustor.android.data.local.*

@Component(modules = arrayOf(RetrofitModule::class,
        RepoModule::class,
        EventBusModule::class,
        LocalStorageModule::class,
        GroupsModule::class))
@AppScope
interface AppComponent {
    val api: ApiGroup
    val repo: RepoGroup

    val context: Context
    val constants: EdustorConstants
    val eventBus: Bus
    val preferences: EdustorPreferences
    val activeSession: ActiveSession
    val syncManager: SyncManager
    val pdfSyncManager: PdfSyncManager
}