package ru.wutiarn.edustor.android.dagger.component

import com.squareup.otto.Bus
import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.module.*
import ru.wutiarn.edustor.android.dagger.pojo.EdustorConstants
import ru.wutiarn.edustor.android.data.api.*
import ru.wutiarn.edustor.android.data.local.ActiveSession
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

@Component(modules = arrayOf(RetrofitModule::class,
        EventBusModule::class,
        BuildTypeConfigModule::class,
        ConstantsModule::class,
        LocalStorageModule::class))
@AppScope
interface AppComponent {
    val constants: EdustorConstants
    val documentsApi: DocumentsApi
    val lessonsApi: LessonsApi
    val subjectsApi: SubjectsApi
    val loginApi: LoginApi
    val syncApi: SyncApi
    val eventBus: Bus
    val preferences: EdustorPreferences
    val activeSession: ActiveSession
}