package ru.wutiarn.edustor.android.dagger.module

import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope

@Module
class EventBusModule {
    @Provides
    @AppScope
    fun eventBus(): Bus {
        return Bus("global-bus")
    }
}
