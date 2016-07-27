package ru.wutiarn.edustor.android.dagger.module

import android.accounts.Account
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.pojo.EdustorConstants
import javax.inject.Named

@Module
class ConstantsModule {
    @Provides
    @AppScope
    fun edustorConstants(@Named("EDUSTOR_URL") url: String, context: Context): EdustorConstants {
        val account = Account("Edustor sync", "edustor.ru")
        return EdustorConstants(url, account)
    }
}
