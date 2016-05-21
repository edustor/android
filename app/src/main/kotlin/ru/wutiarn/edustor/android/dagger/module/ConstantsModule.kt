package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import javax.inject.Named

@Module
class ConstantsModule {
    @Provides
    @AppScope
    fun edustorConstants(@Named("EDUSTOR_URL") url: String): EdustorConstants {
        return EdustorConstants(url)
    }
}

class EdustorConstants(
        var URL: String
)
