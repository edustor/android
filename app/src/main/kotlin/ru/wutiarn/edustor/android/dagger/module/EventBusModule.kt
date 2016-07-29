package ru.wutiarn.edustor.android.dagger.module

import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope

@Module
class EventBusModule {

    companion object {
        private val bus = Bus("global-bus")
    }

    @Provides
    @AppScope
    fun eventBus(): Bus {
        return bus
    }
}
