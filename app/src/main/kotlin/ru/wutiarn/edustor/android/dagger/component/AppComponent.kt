package ru.wutiarn.edustor.android.dagger.component

import dagger.Component
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.module.RetrofitModule
import ru.wutiarn.edustor.android.data.repository.DocumentsRepository

/**
 * Created by wutiarn on 03.03.16.
 */
@Component(modules = arrayOf(RetrofitModule::class))
@AppScope
interface AppComponent {
    var documentsRepository: DocumentsRepository
}