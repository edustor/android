package ru.wutiarn.edustor.android.dagger.component

import com.squareup.otto.Bus
import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.module.*
import ru.wutiarn.edustor.android.dagger.pojo.EdustorConstants
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import ru.wutiarn.edustor.android.data.api.LessonsApi
import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SubjectsApi
import ru.wutiarn.edustor.android.data.local.EdustorPreferences

@Component(modules = arrayOf(RetrofitModule::class,
        EventBusModule::class,
        BuildTypeConfigModule::class,
        ConstantsModule::class,
        PreferencesModule::class))
@AppScope
interface AppComponent {
    val constants: EdustorConstants
    val documentsApi: DocumentsApi
    val lessonsApi: LessonsApi
    val subjectsApi: SubjectsApi
    val loginApi: LoginApi
    val eventBus: Bus
    val preferences: EdustorPreferences
}