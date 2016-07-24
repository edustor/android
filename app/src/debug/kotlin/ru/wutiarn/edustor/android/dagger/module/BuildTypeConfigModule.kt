package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import javax.inject.Named

@Module
open class BuildTypeConfigModule {
    @Provides
    @AppScope
    @Named("EDUSTOR_URL")
    open fun url(): String {
        return "https://edustor.ru/"
    }
}