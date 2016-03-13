package ru.wutiarn.edustor.android.dagger.component

import com.squareup.otto.Bus
import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.module.BuildTypeConfigModule
import ru.wutiarn.edustor.android.dagger.module.EventBusModule
import ru.wutiarn.edustor.android.dagger.module.RetrofitModule
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import ru.wutiarn.edustor.android.data.api.LessonsApi
import ru.wutiarn.edustor.android.data.api.SubjectsApi

/**
 * Created by wutiarn on 03.03.16.
 */
@Component(modules = arrayOf(RetrofitModule::class, EventBusModule::class, BuildTypeConfigModule::class))
@AppScope
interface AppComponent {
    val documentsApi: DocumentsApi
    val lessonsApi: LessonsApi
    val subjectsApi: SubjectsApi
    val eventBus: Bus
}