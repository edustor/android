package ru.wutiarn.edustor.android.dagger.component

import com.squareup.otto.Bus
import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.module.BuildTypeConfigModule
import ru.wutiarn.edustor.android.dagger.module.EventBusModule
import ru.wutiarn.edustor.android.dagger.module.RetrofitModule
import ru.wutiarn.edustor.android.data.api.DocumentsApi
import ru.wutiarn.edustor.android.data.api.LessonsApi

/**
 * Created by wutiarn on 03.03.16.
 */
@Component(modules = arrayOf(RetrofitModule::class, EventBusModule::class, BuildTypeConfigModule::class))
@AppScope
interface AppComponent {
    var documentsApi: DocumentsApi
    var lessonsApi: LessonsApi
    var eventBus: Bus
}