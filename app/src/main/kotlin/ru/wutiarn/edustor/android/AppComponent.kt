package ru.wutiarn.edustor.android

import dagger.Component
import javax.inject.Singleton

/**
 * Created by wutiarn on 03.03.16.
 */
@Component(modules = arrayOf(RetrofitModule::class))
@Singleton
interface AppComponent {
    var documentsRepository: DocumentsRepository
}