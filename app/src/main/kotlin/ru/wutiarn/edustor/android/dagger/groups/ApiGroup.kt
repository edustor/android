package ru.wutiarn.edustor.android.dagger.groups

import ru.wutiarn.edustor.android.data.api.AccountsApi
import ru.wutiarn.edustor.android.data.api.SyncApi

class ApiGroup(
        val accounts: AccountsApi,
        val sync: SyncApi
)