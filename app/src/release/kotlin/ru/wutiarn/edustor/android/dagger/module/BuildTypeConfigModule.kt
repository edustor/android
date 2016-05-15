package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import javax.inject.Named

/**
 * Created by wutiarn on 09.03.16.
 */
@Module
open class BuildTypeConfigModule {
    @Provides
    @AppScope
    @Named("EDUSTOR_URL")
    open fun url(): String {
        return "http://wutiarn.ru/"
    }
}